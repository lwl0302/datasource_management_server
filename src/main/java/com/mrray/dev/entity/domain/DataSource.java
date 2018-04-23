package com.mrray.dev.entity.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ln on 2017/7/21.
 */
@Entity
@Table(name = "t_data_source")
public class DataSource extends SuperEntity {

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    private String sourceType;//"import"代表脱敏源,"export"代表目的源

    private boolean temporary;//true 代表临时源,false代表常用源

    private String dbType;//数据类型,如MYSQL,Oracle,Excel,Csv

    @Column(unique = true, nullable = false, updatable = true)
    private String remark;//源对外的uuid

    private String ip;

    private Integer port;

    private String databaseName;

    private String username;

    private String password;

    private String fileName;

    private String filePath;

    private String comment;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
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

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
