package com.travel.team.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 28/3/2023 下午 2:38
 */
@Data
public class TeamChangeRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 团队 id
     */
    private Long teamId;

    /**
     * 加入 or 退出（0：加入 1：退出 2：踢出）
     */
    private Integer joinOrQuitOrKick;

    private static final long serialVersionUID = 1L;
}
