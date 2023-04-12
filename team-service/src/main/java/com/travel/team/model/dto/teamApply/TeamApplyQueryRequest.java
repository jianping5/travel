package com.travel.team.model.dto.teamApply;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 10/4/2023 下午 6:31
 */
@Data
public class TeamApplyQueryRequest extends PageRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 关键词
     */
    private String searchText;

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
