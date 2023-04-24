package com.travel.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zgy
 * @create 2023-04-03 16:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 所管官方 id
     */
    private Long officialId;

    /**
     * 官方类型 id
     */
    private Integer typeId;

    /**
     * 获赞数
     */
    private Integer likeCount;

    /**
     * 浏览数
     */
    private Integer viewCount;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 头像 URL
     */
    private String userAvatar;

    /**
     * 用户昵称
     */
    private String userName;

    private static final long serialVersionUID = 1L;
}
