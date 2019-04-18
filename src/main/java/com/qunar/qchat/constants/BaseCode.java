package com.qunar.qchat.constants;

/**
 * Created by binz.zhang on 2018/12/28.
 */
public enum BaseCode {

    OK(200, "操作成功"),
    ERROR(500, "服务器异常"),
    DB_ERROR(501, "数据库操作异常"),
    BADREQUEST(400, "参数不合法"),
    OP_RESOURCE_NOTFOUND(404, "操作资源不存在"),
    OP_NOT_SUPPORT(405, "不支持的操作"),
    ;

    private int code;

    private String msg;

    BaseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
