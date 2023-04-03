package com.travel.team.model.dto.news;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:05
 */
@Data
public class TeamNewsAddRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 团队 id
     */
    private Long teamId;

    /**
     * 动态内容
     */
    private String content;

    /**
     * 图片 URL
     */
    private String imageUrl;


    private static final long serialVersionUID = 1L;
}
