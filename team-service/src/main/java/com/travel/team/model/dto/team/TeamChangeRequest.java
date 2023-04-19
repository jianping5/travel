package com.travel.team.model.dto.team;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 改变团队请求体
 * @author jianping5
 * @createDate 28/3/2023 下午 2:38
 */
@Data
@ApiModel(value = "团队改变请求体")
public class TeamChangeRequest implements Serializable {

    /**
     * 用户 id（仅踢人时传输）
     */
    @ApiModelProperty(value = "用户 id（仅踢人时传输）")
    private Long userId;

    /**
     * 团队 id
     */
    @ApiModelProperty(value = "团队 id", required = true)
    private Long teamId;

    /**
     * 加入 or 退出（0：加入 1：退出 2：踢出）
     */
    @ApiModelProperty(value = "加入 or 退出（0：加入 1：退出 2：踢出）", required = true)
    private Integer joinOrQuitOrKick;

    private static final long serialVersionUID = 1L;
}
