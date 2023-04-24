package com.travel.common.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求体
 *
 * @author jianping5
 */
@Data
@ApiModel(value = "删除请求体")
public class DeleteRequest implements Serializable {

    /**
     * "删除对象 id
     */
    @ApiModelProperty(value = "删除对象 id")
    private Long id;

    private static final long serialVersionUID = 1L;
}