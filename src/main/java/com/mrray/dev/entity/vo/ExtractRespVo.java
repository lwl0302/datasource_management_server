package com.mrray.dev.entity.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ln on 2017/8/8.
 */
public class ExtractRespVo extends BaseResourceInfoVo {
    private String extractId;

    public String getExtractId() {
        return extractId;
    }

    public void setExtractId(String extractId) {
        this.extractId = extractId;
    }

    private List<String> tableNames = new ArrayList<String>();

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }
}
