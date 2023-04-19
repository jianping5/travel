package com.travel.common.model.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zgy
 * @create 2023-04-03 22:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO implements Serializable {


    /**
     * 主键
     */
    private Long id;

    /**
     * 详情id
     */
    private Long detailId;

    /**
     * 所属用户 id
     */
    private Long userId;

    /**
     * 头像 URL
     */
    private String userAvatar;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面 URL
     */
    private String coverUrl;

    /**
     * 所属团队 id
     */
    private Long teamId;

    /**
     * 权限（0：公开 1：部分可见 2：私密）
     */
    private Integer permission;

    /**
     * 标签
     */
    private String tag;

    /**
     * 文章首句话
     */
    private String intro;

    /**
     * 文章详情
     */
    private String detail;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 点赞量
     */
    private Integer likeCount;

    /**
     * 评论量
     */
    private Integer commentCount;

    /**
     * 收藏量
     */
    private Integer favoriteCount;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 是否已点赞
     */
    private Integer isLiked;

    /**
     * 是否已收藏
     */
    private Integer isCollected;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
