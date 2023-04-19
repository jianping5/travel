package com.travel.team.model.dto.teamApply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 团队申请更新请求体
 * @author jianping5
 * @createDate 10/4/2023 下午 5:57
 */
@Data
@ApiModel(value = "团队申请更新请求体")
public class TeamApplyUpdateRequest implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "团队申请 id", required = true)
    private Long id;

    /**
     * 用户 id
     */
    @ApiModelProperty(value = "用户 id", required = true)
    private Long userId;

    /**
     * 团队 id
     */
    @ApiModelProperty(value = "团队 id", required = true)
    private Long teamId;

    /**
     * 申请状态（0：审批中 1：同意 2：拒绝）
     */
    @ApiModelProperty(value = "申请状态（0：审批中 1：同意 2：拒绝）", required = true)
    private Integer auditState;

    /**
     * 团队状态（0：正常 1：异常 2：已解散）
     */
    @ApiModelProperty(value = "团队状态（0：正常 1：异常 2：已解散）")
    private Integer teamState;

    private static final long serialVersionUID = 1L;

}
