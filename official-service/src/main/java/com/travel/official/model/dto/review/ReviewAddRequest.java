package com.travel.official.model.dto.review;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "点评添加请求体")
public class ReviewAddRequest implements Serializable {

    /**
     * 点评 id
     */
    @ApiModelProperty(value = "点评 id")
    private Long id;

    /**
     * 用户 id
     */
    @ApiModelProperty(value = "用户 id")
    private Long userId;

    /**
     * 点评对象类型（1：景区 2：酒店 3：美食）
     */
    @ApiModelProperty(value = "点评对象类型", required = true)
    private Integer reviewObjType;

    /**
     * 点评对象 id
     */
    @ApiModelProperty(value = "点评对象 id", required = true)
    private Long reviewObjId;

    /**
     * 点评内容
     */
    @ApiModelProperty(value = "点评内容", required = true)
    private String content;

    /**
     * 地理位置
     */
    @ApiModelProperty(value = "地理位置")
    private String location;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;

}
