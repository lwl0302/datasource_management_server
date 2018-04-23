package com.mrray.dev.entity.dto;

import com.mrray.dev.entity.vo.BaseResourceInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by ln on 2017/7/19.
 */
@ApiModel(description = "源模型")
public class DataSourceDto extends BaseResourceInfoVo {

    @ApiModelProperty(value = "import代表脱敏源,export代表目的源")
    private String sourceType;

    @ApiModelProperty(value = "true代表临时源,false代表常用源")
    private boolean temporary;

    @ApiModelProperty(value = "常用源的备注信息")
    private String comment;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
