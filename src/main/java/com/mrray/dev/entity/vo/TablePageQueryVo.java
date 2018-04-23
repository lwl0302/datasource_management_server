package com.mrray.dev.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Created by ln on 2017/7/26.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TablePageQueryVo extends PageQueryVo<Map> {

    private String id;

    private String ip;

    private Integer port;

    private String username;

    private String password;

    private String databaseName;

    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
