package com.qunar.qchat.dao;

import com.qunar.qchat.dao.model.HostUserModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IHostUserDao {

    List<HostUserModel> selectIncrementByVersion(@Param("table") String table,
                                                 @Param("version") Integer version,
                                                 @Param("hostId") Integer hostId);

    /**
     * 查询用户信息
     * @param table
     * @param userId
     * @param hostId
     * @return
     */
    HostUserModel getHostUser(@Param("table") String table,
                              @Param("userId") String userId,
                              @Param("hostId") Integer hostId);

    /**
     * 新增用户信息
     * @param table
     * @param userId
     * @param hostId
     * @param name
     * @param tel
     * @param email
     * @return
     */
    HostUserModel addHostUser(@Param("table") String table,
                                 @Param("userId") String userId,
                                 @Param("hostId") Integer hostId,
                                 @Param("name") String name,
                                 @Param("tel") String tel,
                                 @Param("email") String email);

    /**
     * 更新用户信息
     * @param table
     * @param userId
     * @param hostId
     * @param userName
     * @param tel
     * @param email
     * @return
     */
    HostUserModel updateHostUser(@Param("table") String table,
                                 @Param("userId") String userId,
                                 @Param("hostId") Integer hostId,
                                 @Param("userName") String userName,
                                 @Param("tel") String tel,
                                 @Param("email") String email);

    Integer selectMaxVersion(@Param("table") String table);

}
