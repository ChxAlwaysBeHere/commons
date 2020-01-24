package org.chx.commons.web.response;

/**
 * @author chenxi
 * @date 2020-01-20
 */
public class HttpResponse<T> {

    private Integer code;

    private String msg;

    private T data;

    public HttpResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public HttpResponse(StatusCode statusCode, String msg, T data) {
        this.code = statusCode.getCode();
        this.msg = (msg == null ? statusCode.getMsg() : statusCode.getMsg(msg));
        this.data = data;
    }

    public static <T> HttpResponse<T> success(T data) {
        return new HttpResponse<T>(StatusCode.SUCCESS, null, data);
    }

    public static <T> HttpResponse<T> error(StatusCode statusCode, String errorMsg) {
        return new HttpResponse<T>(statusCode, errorMsg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
