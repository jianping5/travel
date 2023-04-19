package com.travel.team.model.dto.wall;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 团队墙添加请求体
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "团队墙添加请求体")
public class TeamWallAddRequest implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 当前登录用户 id（不传）
     */
    @ApiModelProperty(value = "当前登录用户 id（不传）")
    private Long userId;

    /**
     * 团队 id
     */
    @ApiModelProperty(value = "主键", required = true)
    private Long teamId;

    /**
     * 墙内容
     */
    @ApiModelProperty(value = "主键", required = true)
    private String content;


    private static final long serialVersionUID = 1L;
}
