package com.travel.team.model.dto.team;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加团队请求体
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "团队添加请求体")
public class TeamAddRequest implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "团队 id")
    private Long id;

    /**
     * 创建人 id
     */
    @ApiModelProperty(value = "创建人 id")
    private Long userId;

    /**
     * 团队名
     */
    @ApiModelProperty(value = "团队名", required = true)
    private String teamName;

    /**
     * 团队图标 URL
     */
    @ApiModelProperty(value = "团队图标 URL", required = true)
    private String iconUrl;

    /**
     * 团队封面 URL
     */
    @ApiModelProperty(value = "团队封面 URL", required = true)
    private String coverUrl;

    /**
     * 团队介绍
     */
    @ApiModelProperty(value = "团队介绍", required = true)
    private String intro;

    /**
     * 团队容量
     */
    @ApiModelProperty(value = "团队容量", required = true)
    private Integer capacity;

    /**
     * 是否需要审核（0：不需要 1：需要）
     */
    @ApiModelProperty(value = "是否需要审核（0：不需要 1：需要）", required = true)
    private Integer isAudit;

    private static final long serialVersionUID = 1L;
}
