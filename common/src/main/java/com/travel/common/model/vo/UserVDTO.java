package com.travel.common.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 12/4/2023 下午 4:27
 */
@Data
public class UserVDTO implements Serializable {

    /**
     * 用户信息 id
     */
    private Long id;

    /**
     * 用户 id（user 中的 id）
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 获赞数
     */
    private Integer likeNum;

    /**
     * 关注数
     */
    private Integer followCount;

    private static final long serialVersionUID = 1L;
}
