package com.travel.common.common;

import com.travel.common.constant.CommonConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页请求
 * @author jianping5
 */
@Data
@ApiModel(value = "分页请求体")
public class PageRequest {

    /**
     * 当前页号
     */
    @ApiModelProperty(value = "当前页号")
    private long current = 1;

    /**
     * 页面大小
     */
    @ApiModelProperty(value = "页面大小")
    private long pageSize = 10;

    /**
     * 排序字段（String SORT_ORDER_ALL = "all";）
     */
    @ApiModelProperty(value = "排序字段（综合排序：all）", required = true)
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    @ApiModelProperty(value = "排序顺序（默认降序）")
    private String sortOrder = CommonConstant.SORT_ORDER_DESC;
}
