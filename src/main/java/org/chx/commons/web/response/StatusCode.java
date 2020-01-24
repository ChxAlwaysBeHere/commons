package org.chx.commons.web.response;

/**
 * @author chenxi
 * @date 2020-01-20
 */
public enum StatusCode {

    SUCCESS(0, "服务运行成功"),

    ERROR(-1, "%s");

    private int code;

    private String msg;

    StatusCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public String getMsg() {
        return msg;
    }

    public String getMsg(String errorMsg) {
        return String.format(this.msg, errorMsg);
    }
}
