package com.travel.user.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class UserLikeRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 点赞类型
     */
    private Integer likeObjType;

    /**
     * 点赞对象 id
     */
    private Long likeObjId;

    /**
     * 点赞状态（0：点赞 1：取消点赞）
     */
    private Integer likeState;


    private static final long serialVersionUID = 1L;

}
