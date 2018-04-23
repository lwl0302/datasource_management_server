package com.mrray.dev.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Arthur on 2017/7/19.
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RestResponseBody<T> {

    private String message;

    private String error;

    private T data;

    public RestResponseBody() {
        this.message = RestResponseMessage.SUCCESS.getMsg();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
