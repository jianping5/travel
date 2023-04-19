package com.travel.team.model.dto.teamApply;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 团队申请查询请求体
 * @author jianping5
 * @createDate 10/4/2023 下午 6:31
 */
@Data
@ApiModel(value = "团队申请查询请求体")
public class TeamApplyQueryRequest extends PageRequest implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词")
    private String searchText;

    /**
     * 用户 id
     */
    @ApiModelProperty(value = "用户 id")
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
