package com.travel.team.model.dto.teamApply;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 10/4/2023 下午 5:57
 */
@Data
public class TeamApplyUpdateRequest implements Serializable {

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
     * 申请状态（0：审批中 1：同意 2：拒绝）
     */
    private Integer auditState;

    /**
     * 团队状态（0：正常 1：异常 2：已解散）
     */
    private Integer teamState;

    private static final long serialVersionUID = 1L;

}
