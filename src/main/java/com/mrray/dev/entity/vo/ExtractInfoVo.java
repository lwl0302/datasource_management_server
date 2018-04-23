package com.mrray.dev.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

/**
 * Created by ln on 2017/7/21.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExtractInfoVo extends BaseResourceInfoVo {

    private Long id;

    private List<Map> tablesMap = new ArrayList<Map>();

    private Set<String> tables = new HashSet<String>();

    public List<Map> getTablesMap() {
        return tablesMap;
    }

    public void setTablesMap(List<Map> tablesMap) {
        this.tablesMap = tablesMap;
    }

    public Set<String> getTables() {
        return tables;
    }

    public void setTables(Set<String> tables) {
        this.tables = tables;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
