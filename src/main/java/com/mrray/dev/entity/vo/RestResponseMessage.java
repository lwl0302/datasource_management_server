package com.mrray.dev.entity.vo;

/**
 * Created by Arthur on 2017/7/19.
 */
public enum  RestResponseMessage {

    SUCCESS("SUCCESS", "请求成功");

    private String msg;
    private String info;

    RestResponseMessage(String msg, String info) {
        this.msg = msg;
        this.info = info;
    }

    public String getMsg() {
        return msg;
    }

    public String getInfo() {
        return info;
    }

    public static RestResponseMessage getStatus(String msg) {
        for (RestResponseMessage value : values()) {
            if (value.msg.equals(msg)) {
                return value;
            }
        }
        return SUCCESS;
    }

}
