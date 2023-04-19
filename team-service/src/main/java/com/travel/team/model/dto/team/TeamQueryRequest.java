package com.travel.team.model.dto.team;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 团队查询请求体
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
@ApiModel(value = "团队查询请求体")
public class TeamQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 创建人 id
     */
    @ApiModelProperty(value = "创建人 id")
    private Long userId;

    /**
     * 搜索词
     */
    @ApiModelProperty(value = "搜索词")
    private String searchText;

    /**
     * 团队名
     */
    @ApiModelProperty(value = "团队名")
    private String teamName;

    /**
     * 团队介绍
     */
    @ApiModelProperty(value = "团队介绍")
    private String intro;

    /**
     * 是否需要审核（0：不需要 1：需要）
     */
    @ApiModelProperty(value = "是否需要审核（0：不需要 1：需要）")
    private Integer isAudit;

    /**
     * 团队状态（0：正常 1：异常 2：已解散）
     */
    @ApiModelProperty(value = "团队状态（0：正常 1：异常 2：已解散）")
    private Integer teamState;

    private static final long serialVersionUID = 1L;
}
