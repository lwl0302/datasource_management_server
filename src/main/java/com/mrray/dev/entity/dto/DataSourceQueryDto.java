package com.mrray.dev.entity.dto;

/**
 * Created by ln on 2017/7/24.
 */
public class DataSourceQueryDto extends PageQueryDto {

    private String sourceType = "import";

    private String query;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
