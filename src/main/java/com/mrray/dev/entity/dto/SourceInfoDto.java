package com.mrray.dev.entity.dto;

import com.mrray.dev.entity.vo.BaseResourceInfoVo;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by ln on 2017/7/20.
 */
public class SourceInfoDto extends BaseResourceInfoVo{

    @NotBlank(message = "TABLE_NAME_NOT_NULL")
    private String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
