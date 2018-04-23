package com.mrray.dev.entity.dto;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * Created by ln on 2017/7/26.
 */
public class TablesPageQueryDto {
    @Min(value = 1, message = "PAGE_LT_ONE")
    private int page = 1;

    /**
     * 分页大小
     */
    @Range(min = 3, max = 30, message = "SIZE_NOTIN_RANGE")
    private int size = 10;

    private String tableName;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
