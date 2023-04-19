package com.travel.user.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class CollectionRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 收藏夹 id
     */
    private Long favoriteId;

    /**
     * 收藏对象类型
     */
    private Integer collectionObjType;

    /**
     * 收藏对象 id
     */
    private Long collectionObjId;
    /**
     * 请求类型（0：取消收藏，1：收藏）
     */
    private Integer requestType;


    private static final long serialVersionUID = 1L;

}
