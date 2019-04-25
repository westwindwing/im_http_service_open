package com.qunar.qchat.dao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * UserInfoQtalk
 *
 * @author binz.zhang
 * @date 2018/10/12
 */
@Getter
@Setter
public class UserInfoQtalk {

    @JsonIgnore
    private long host_id = 1;

    @JsonProperty("U")
    private String user_id;

    @JsonIgnore
    private String password = "";

    @JsonProperty("N")
    private String user_name;

    @JsonProperty("D")
    private String department = "";

    @JsonIgnore
    private String ps_deptid = "";

    @JsonIgnore
    private String dep1 = "";

    @JsonIgnore
    private String dep2 = "";

    @JsonIgnore
    private String dep3 = "";

    @JsonIgnore
    private String dep4 = "";

    @JsonIgnore
    private String dep5 = "";

    @JsonProperty("pinyin")
    private String pinyin = "";

    @JsonIgnore
    private String joinDate;

    @JsonIgnore
    private Integer hire_flag;

    @JsonIgnore
    private Integer version;

    @JsonProperty("sex")
    public Integer gender = 1;

    @JsonIgnore
    public String sex;

    @JsonProperty("uType")
    public String user_type = "";

    @JsonProperty("email")
    public String email = "";

    @JsonIgnore
    private int frozen_flag = 1;

    @JsonIgnore
    private String leader;

}
