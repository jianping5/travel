package com.travel.data.model.dto;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 搜索请求体
 * 查询请求
 * @author jianping5
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "搜索请求体")
public class SearchRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    @ApiModelProperty(value = "搜索词", required = true)
    private String searchText;

    /**
     * 类型名（例如：搜团队->team）
     */
    @ApiModelProperty(value = "类型名（例如：团队->team）", required = true)
    private String type;

    /**
     * 分类类型 id（1：景区 2：酒店 3：官方）
     */
    @ApiModelProperty(value = "分类类型 id（1：景区 2：酒店 3：官方）")
    private Integer classifyTypeId;

    private static final long serialVersionUID = 1L;
}