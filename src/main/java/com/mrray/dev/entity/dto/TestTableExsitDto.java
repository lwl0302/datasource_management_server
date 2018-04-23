package com.mrray.dev.entity.dto;

import com.mrray.dev.entity.vo.BaseResourceInfoVo;

/**
 * Created by ln on 2017/8/3.
 */
public class TestTableExsitDto extends BaseResourceInfoVo {
    private String id;

    private String tableName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
