package com.mrray.dev.entity.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ln on 2017/7/21.
 */

@Entity
@Table(name = "t_task")
public class Task extends SuperEntity {

    @Column(unique = true, nullable = false, updatable = true)
    private String remark;

    private String status;//任务状态,fail代表失败,success代表成功,doing代表正在进行

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    private Integer type;//任务类型,0为抽取任务,1为装载任务,2为批量抽取整个数据库

    private Long sourceId;//源id

    private Long targetId;//目的源id

    private String sourceIp;

    private String targetIp;

    private Integer sourcePort;

    private Integer targetPort;

    private String sourceDatabaseName;

    private String targetDatabaseName;

    @Column(columnDefinition="TEXT",length = 65535)
    private String sourceTableName;

    //@Type(type="text")这个是longtext
    @Column(columnDefinition="TEXT",length = 65535)
    private String targetTableName;

    private String sourceType;

    private String targetType;

    private String sourceFileName;

    private String targetFilename;

    private String sourceFilepath;

    private String targetFilePath;

    @Column(columnDefinition="TEXT",length = 65535)
    private String createTableSql;//抽取任务,存入源表ddl,装载任务,存入目标表ddl

    private String failCasuse;

    public String getFailCasuse() {
        return failCasuse;
    }

    public void setFailCasuse(String failCasuse) {
        this.failCasuse = failCasuse;
    }

    public String getCreateTableSql() {
        return createTableSql;
    }

    public void setCreateTableSql(String createTableSql) {
        this.createTableSql = createTableSql;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public Integer getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(Integer sourcePort) {
        this.sourcePort = sourcePort;
    }

    public Integer getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(Integer targetPort) {
        this.targetPort = targetPort;
    }

    public String getSourceDatabaseName() {
        return sourceDatabaseName;
    }

    public void setSourceDatabaseName(String sourceDatabaseName) {
        this.sourceDatabaseName = sourceDatabaseName;
    }

    public String getTargetDatabaseName() {
        return targetDatabaseName;
    }

    public void setTargetDatabaseName(String targetDatabaseName) {
        this.targetDatabaseName = targetDatabaseName;
    }

    public String getSourceTableName() {
        return sourceTableName;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getTargetFilename() {
        return targetFilename;
    }

    public void setTargetFilename(String targetFilename) {
        this.targetFilename = targetFilename;
    }

    public String getSourceFilepath() {
        return sourceFilepath;
    }

    public void setSourceFilepath(String sourceFilepath) {
        this.sourceFilepath = sourceFilepath;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
