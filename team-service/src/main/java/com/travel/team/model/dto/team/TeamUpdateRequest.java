package com.travel.team.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class TeamUpdateRequest implements Serializable {


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
     * 团队容量
     */
    private Integer capacity;

    /**
     * 是否需要审核（0：不需要 1：需要）
     */
    private Integer isAudit;

    /**
     * 团队状态（0：正常 1：异常 2：已解散）
     */
    private Integer teamState;

    private static final long serialVersionUID = 1L;
}
