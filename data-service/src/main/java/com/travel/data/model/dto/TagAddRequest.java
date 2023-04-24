package com.travel.data.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 13/4/2023 下午 7:02
 */
@Data
@ApiModel(value = "标签添加请求体")
public class TagAddRequest implements Serializable {

    /**
     * 标签数组
     */
    @ApiModelProperty(value = "标签数组", required = true)
    String tagList;

    /**
     * 标签类型
     */
    @ApiModelProperty(value = "标签类型", required = true)
    Integer tagType;

    private static final long serialVersionUID = 1L;

}
