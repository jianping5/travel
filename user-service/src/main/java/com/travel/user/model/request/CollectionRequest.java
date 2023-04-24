package com.travel.user.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "收藏/取消收藏请求体")
public class CollectionRequest implements Serializable {

    @ApiModelProperty(hidden = true)
    /**
     * 用户 id
     */
    private Long userId;

    @ApiModelProperty(value = "收藏夹 id",required = true)
    /**
     * 收藏夹 id
     */
    private Long favoriteId;

    @ApiModelProperty(value = "收藏对象类型",required = true)
    /**
     * 收藏对象类型
     */
    private Integer collectionObjType;

    @ApiModelProperty(value = "收藏对象 id",required = true)
    /**
     * 收藏对象 id
     */
    private Long collectionObjId;

    @ApiModelProperty(value = "请求类型（0：取消收藏，1：收藏）",required = true)
    /**
     * 请求类型（0：取消收藏，1：收藏）
     */
    private Integer requestType;


    private static final long serialVersionUID = 1L;

}
