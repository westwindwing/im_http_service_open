package com.qunar.qchat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.qunar.qchat.constants.Config;
import com.qunar.qchat.model.JsonResult;
import com.qunar.qchat.service.QtalkUpdateStructService;
import com.qunar.qchat.utils.CookieUtils;
import com.qunar.qchat.utils.HttpClientUtils;
import com.qunar.qchat.utils.JacksonUtils;
import com.qunar.qchat.utils.JsonResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * QtalkUpdateStructure
 *
 * @author binz.zhang
 * @date 2018/11/26
 */
@Controller
@RequestMapping("/newapi/update/")
public class QtalkUpdateStructure {
    private static final Logger LOGGER = LoggerFactory.getLogger(QtalkUpdateStructure.class);

    @Autowired
    private QtalkUpdateStructService qchatUpdateStructService;

    @ResponseBody
    @RequestMapping(value = "/getUpdateUsers.qunar", method = RequestMethod.POST)
    public JsonResult<?> updateStructure(HttpServletRequest request, @RequestBody String param) {
        Map<String, Object> qckey = CookieUtils.getUserbyCookie(request);
        String domain = (String) qckey.get("d");
        if (Strings.isNullOrEmpty(domain)) {
            return JsonResultUtils.fail(1, "请指定域");
        }
        JSONObject receivedParam = JSON.parseObject(param);
        Integer clientUserInfoVersion = (Integer) receivedParam.get("version");
        if (clientUserInfoVersion == null || clientUserInfoVersion < 0) {
            return JsonResultUtils.fail(1, "无效version");
        }
        LOGGER.info("get the users info the client users info version is：{},domain is：{}", clientUserInfoVersion, domain);
        return qchatUpdateStructService.getQtalk(clientUserInfoVersion, domain);
    }

    /**
     * 根据用户所属角色，过滤可以访问的组织机构成员信息
     * 管理员可以访问所有成员
     * 其他角色只能访问userRole为admin(管理员)和srv(客服)的成员
     * @param request
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUpdateUsersByUserRole.qunar", method = RequestMethod.POST)
    public JsonResult<?> getUpdateUsersByUserRole(HttpServletRequest request, @RequestBody String param) {
        Map<String, Object> qckey = CookieUtils.getUserbyCookie(request);
        String domain = (String) qckey.get("d");
        if (Strings.isNullOrEmpty(domain)) {
            return JsonResultUtils.fail(1, "请指定域");
        }
        JSONObject receivedParam = JSON.parseObject(param);
        Integer clientUserInfoVersion = (Integer) receivedParam.get("version");
        if (clientUserInfoVersion == null || clientUserInfoVersion < 0) {
            return JsonResultUtils.fail(1, "无效version");
        }
        LOGGER.info("get the users info the client users info version is：{},domain is：{}", clientUserInfoVersion, domain);
        return qchatUpdateStructService.getQtalk(clientUserInfoVersion, domain);
    }


    @ResponseBody
    @RequestMapping(value = "/triggerNotify.qunar", method = RequestMethod.POST)
    public JsonResult<?> trigger(HttpServletRequest request, @RequestBody String param) {
        JSONObject receivedParam = JSON.parseObject(param);
        Integer hostId = (Integer) receivedParam.get("hostId");
        if (hostId == null || hostId < 0) {
            return JsonResultUtils.fail(1, "hostId 无效");
        }
        LOGGER.info("trigger send notify hostId:{}", hostId);
        if (qchatUpdateStructService.triggerSend(hostId)) {
            return JsonResultUtils.success();
        }
        return JsonResultUtils.fail(500,"服务器错误");
    }
}