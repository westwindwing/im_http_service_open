package com.qunar.qchat.controller;

import com.qunar.qchat.constants.Config;
import com.qunar.qchat.dao.IMucInfoDao;
import com.qunar.qchat.dao.model.MucIncrementInfo;
import com.qunar.qchat.dao.model.MucInfoModel;
import com.qunar.qchat.model.JsonResult;
import com.qunar.qchat.model.request.GetIncrementMucsRequest;
import com.qunar.qchat.model.request.GetMucVcardRequest;
import com.qunar.qchat.model.request.UpdateMucNickRequest;
import com.qunar.qchat.model.result.GetMucVcardResult;
import com.qunar.qchat.model.result.UpdateMucNickResult;
import com.qunar.qchat.utils.CookieUtils;
import com.qunar.qchat.utils.HttpClientUtils;
import com.qunar.qchat.utils.JsonResultUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/newapi/muc/")
@RestController
public class QMucInfoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QMucInfoController.class);

    @Autowired
    private IMucInfoDao iMucInfoDao;


    /**
     * 获取新增群列表.
     * @param httpRequest HttpServletRequest
     * @param paramRequest GetIncrementMucsRequest
     * @return  JsonResult<?>
     * */
    @RequestMapping(value = "/get_increment_mucs.qunar", method = RequestMethod.POST)
    public Object getIncrement(HttpServletRequest httpRequest,
                                      @RequestBody GetIncrementMucsRequest paramRequest) {
        try {
            if(Objects.isNull(paramRequest.getT())) {
                return JsonResultUtils.fail(1, "参数错误");
            }

            if(StringUtils.isBlank(paramRequest.getU())) {
                Map<String, Object> cookie = CookieUtils.getUserbyCookie(httpRequest);
                paramRequest.setU(cookie.get("u").toString());
            }
            if(StringUtils.isBlank(paramRequest.getD())) {
                Map<String, Object> cookie = CookieUtils.getUserbyCookie(httpRequest);
                paramRequest.setD(cookie.get("d").toString());
            }

            /**
             * 解决pg to_timestamp 只接受秒数的问题.
             * */
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String strTime = sdf.format(paramRequest.getT());

            List<MucIncrementInfo> mucIncrementInfoList = iMucInfoDao.selectMucIncrementInfoNew(paramRequest.getU(), paramRequest.getD(), strTime);


            List<Map<String, Object>> result = new ArrayList<>();
            mucIncrementInfoList.stream().forEach(item -> {
                Map<String, Object> map = new HashMap<>();
                map.put("M", StringUtils.defaultString(item.getMuc_name(), ""));
                map.put("D", StringUtils.defaultString(item.getDomain(), ""));
                map.put("T", StringUtils.defaultString(String.valueOf(item.getT()), ""));
                map.put("F", item.getRegisted_flag());
                result.add(map);
            });

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("ret", true);
            resultMap.put("errcode", 0);
            resultMap.put("errmsg", "");
            if(CollectionUtils.isNotEmpty(mucIncrementInfoList)) {
                resultMap.put("version", String.valueOf(mucIncrementInfoList.get(0).getCreated_at().getTime()));
            } else {
                BigDecimal bigDecimal = new BigDecimal(paramRequest.getT());
                resultMap.put("version", bigDecimal.toString());
            }
            resultMap.put("data", result);

            return resultMap;

        } catch (Exception e) {
            LOGGER.error("catch error : {}", ExceptionUtils.getStackTrace(e));
            return JsonResultUtils.fail(0, "服务器操作异常");
        }
    }

    /**
     * 设置群信息.
     * @param requests List<UpdateMucNickRequest>
     * @return JsonResult<?>
     * */
    @RequestMapping(value = "/set_muc_vcard.qunar", method = RequestMethod.POST)
    public JsonResult<?> updateMucNick(@RequestBody List<UpdateMucNickRequest> requests) {
        try{
            // 校验参数
            if (CollectionUtils.isEmpty(requests)) {
                return JsonResultUtils.fail(1, "参数错误");
            }

            for(UpdateMucNickRequest request : requests) {
                if(!request.isRequestValid()) {
                    return JsonResultUtils.fail(1, "参数错误");
                }
            }


            /*List<MucInfoModel> mucInfoModels = iMucInfoDao.selectMucInfoByIds(requests.stream()
                     .map(request -> request.getMuc_name()).collect(Collectors.toList()));*/

            //fix bug
            for(UpdateMucNickRequest request : requests) {
                String tempMucName = "";
                if (request.getMuc_name().indexOf("@") == -1) {
                    tempMucName = request.getMuc_name();
                } else {
                    tempMucName = request.getMuc_name().substring(0, request.getMuc_name().indexOf("@"));
                }
                int exitCount = iMucInfoDao.checkMucExist(tempMucName);
                if(exitCount == 0) {
                    return JsonResultUtils.fail(1, "群" + request.getMuc_name() + "不存在");
                }
            }

            List<UpdateMucNickResult> resultList = new ArrayList<>();
            for(UpdateMucNickRequest request : requests) {

                //判断群数据是否存在
                Integer mucCount = iMucInfoDao.selectMucCountByMucName(request.getMuc_name());
                if(mucCount == null || mucCount == 0){

                    MucInfoModel mucInfoModel = new MucInfoModel();
                    mucInfoModel.setMucName(request.getMuc_name());
                    mucInfoModel.setShowName(request.getNick());
                    mucInfoModel.setMucTitle(request.getTitle());
                    mucInfoModel.setMucDesc(request.getDesc());
                    iMucInfoDao.insertMucInfo(mucInfoModel);
                } else {
                    MucInfoModel parameter = new MucInfoModel();
                    parameter.setMucName(request.getMuc_name());
                    parameter.setShowName(request.getNick());
                    parameter.setMucTitle(request.getTitle());
                    parameter.setMucDesc(request.getDesc());
                    iMucInfoDao.updateMucInfo(parameter);
                }


                MucInfoModel newMucInfo = iMucInfoDao.selectByMucName(request.getMuc_name());
                UpdateMucNickResult result = new UpdateMucNickResult();
                if (!Objects.isNull(result)) {
                    result.setMuc_name(StringUtils.defaultString(newMucInfo.getMucName(), ""));
                    result.setVersion(StringUtils.defaultString(newMucInfo.getVersion(), ""));
                    result.setShow_name(StringUtils.defaultString(newMucInfo.getShowName(), ""));
                    result.setMuc_title(StringUtils.defaultString(newMucInfo.getMucTitle(), ""));
                    result.setMuc_desc(StringUtils.defaultString(newMucInfo.getMucDesc(), ""));
                }
                resultList.add(result);

                //发送通知
                HttpClientUtils.get(Config.UPDATE_MUC_VCARD_MSG_URL + "?muc_name=" + result.getMuc_name());
                LOGGER.info("发送群信息变更通知成功，群ID : {}", request.getMuc_name());
            }
            return JsonResultUtils.success(resultList);
        }catch (Exception ex) {
            LOGGER.error("catch error: {}", ExceptionUtils.getStackTrace(ex));
            return JsonResultUtils.fail(0, "服务器操作异常");
        }
    }

    /**
     * 获取群信息.
     * @param request List<GetMucVcardRequest>
     * @return  JsonResult<?>
     * */
    @RequestMapping(value = "/get_muc_vcard.qunar", method = RequestMethod.POST)
    public JsonResult<?> getMucVCard(@RequestBody List<GetMucVcardRequest> request) {
        try{
            //LOGGER.info(request.toString());

            //检查参数是否合法
            if (!checkGetMucVCardInfoParams(request)) {
                return JsonResultUtils.fail(0, "参数错误");
            }

            List<GetMucVcardResult> results =
            request.stream().map(item -> {
                List<GetMucVcardRequest.MucInfo> mucInfos = item.getMucs();
                GetMucVcardResult result = new GetMucVcardResult();
                result.setDomain(item.getDomain());
                if(CollectionUtils.isNotEmpty(mucInfos)){
                    List<String> mucIds = mucInfos.stream().
                            map(requestMucInfo -> requestMucInfo.getMuc_name()).collect(Collectors.toList());

                    List<MucInfoModel> mucInfoModels = iMucInfoDao.selectMucInfoByIds(mucIds);
                    List<GetMucVcardResult.MucInfo> mucInfoResultList =
                            mucInfoModels.stream().map(mucInfoModel -> {
                                GetMucVcardResult.MucInfo resultMucInfo = new GetMucVcardResult.MucInfo();
                                resultMucInfo.setMN(StringUtils.defaultString(mucInfoModel.getMucName(), ""));
                                resultMucInfo.setSN(StringUtils.defaultString(mucInfoModel.getShowName(), ""));
                                resultMucInfo.setMD(StringUtils.defaultString(mucInfoModel.getMucDesc(), ""));
                                resultMucInfo.setMT(StringUtils.defaultString(mucInfoModel.getMucTitle(), ""));
                                resultMucInfo.setMP(StringUtils.defaultString(mucInfoModel.getMucPic(), ""));
                                resultMucInfo.setVS(StringUtils.defaultString(mucInfoModel.getVersion(), ""));
                                return resultMucInfo;
                            }).collect(Collectors.toList());
                    result.setMucs(mucInfoResultList);
                }else {
                    result.setMucs(new ArrayList<>());
                }

                return result;
            }).collect(Collectors.toList());

            return JsonResultUtils.success(results);
        }catch (Exception ex) {
            LOGGER.error("catch error : {} ", ExceptionUtils.getStackTrace(ex));
            return JsonResultUtils.fail(0, "服务器操作异常");
        }
    }

    private boolean checkGetMucVCardInfoParams(List<GetMucVcardRequest> request){
        if (CollectionUtils.isEmpty(request)) {
            return false;
        }
        for(GetMucVcardRequest item : request) {
            List<GetMucVcardRequest.MucInfo> mucInfos = item.getMucs();
            for(GetMucVcardRequest.MucInfo info : mucInfos) {
                if (StringUtils.isBlank(info.getMuc_name())) {
                    //return JsonResultUtils.fail(0, "参数错误");
                    return false;
                }
            }
        }
        return true;
    }
}
