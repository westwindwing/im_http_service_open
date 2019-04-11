package com.qunar.qchat.dao;

import com.qunar.qchat.dao.model.HostInfoModel;
import org.apache.ibatis.annotations.Param;

/**
 * @auth dongzd.zhang
 * @Date 2018/11/6 11:02
 */
public interface IHostInfoDao {


    /**
     * 根据host查询.
     * @param host
     * @return HostInfoModel
     * */
    HostInfoModel selectHostInfoByHostName(@Param("domain") String host);

    /**
     * 选取默认host信息
      * @return
     */
    HostInfoModel selectDefaultHost();
}
