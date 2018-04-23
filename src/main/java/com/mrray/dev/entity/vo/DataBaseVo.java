package com.mrray.dev.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by ln on 2017/7/24.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataBaseVo {
    private String id;

    private String ip;

    private String databaseName;

    private String type;

    private String comment;

    private ExtractInfoVo extractInfo = new ExtractInfoVo();

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ExtractInfoVo getExtractInfo() {
        return extractInfo;
    }

    public void setExtractInfo(ExtractInfoVo extractInfo) {
        this.extractInfo = extractInfo;
    }
}
