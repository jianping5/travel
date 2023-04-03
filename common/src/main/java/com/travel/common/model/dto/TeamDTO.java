package com.travel.common.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 团队表
 * @TableName team
 */
@Data
public class TeamDTO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 创建人 id
     */
    private Long userId;

    /**
     * 团队名
     */
    private String teamName;

    /**
     * 团队图标 URL
     */
    private String iconUrl;

    /**
     * 团队封面 URL
     */
    private String coverUrl;

    /**
     * 团队介绍
     */
    private String intro;

    /**
     * 团队人数
     */
    private Integer teamSize;

    /**
     * 团队容量
     */
    private Integer capacity;

    /**
     * 团队动态数
     */
    private Integer newsCount;

    /**
     * 团队游记数
     */
    private Integer travelCount;

    /**
     * 是否需要审核（0：不需要 1：需要）
     */
    private Integer isAudit;

    /**
     * 团队状态（0：正常 1：异常 2：已解散）
     */
    private Integer teamState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}