package com.travel.common.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 * @author jianping5
 */
@Data
@ApiModel(value = "文件上传请求请求体")
public class UploadFileRequest implements Serializable {

    /**
     * 业务名（FileUploadBizEnum.getValue())
     */
    @ApiModelProperty(value = "业务名（FileUploadBizEnum.getValue())")
    private String biz;

    private static final long serialVersionUID = 1L;
}