package com.travel.team.model.dto.wall;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 团队墙查询请求体
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
@ApiModel(value = "团队墙查询请求体")
public class TeamWallQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 用户 id
     */
    @ApiModelProperty(value = "用户 id")
    private Long userId;

    /**
     * 搜索词
     */
    @ApiModelProperty(value = "搜索词")
    private String searchText;

    /**
     * 团队 id
     */
    @ApiModelProperty(value = "团队 id", required = true)
    private Long teamId;

    /**
     * 墙内容
     */
    @ApiModelProperty(value = "主键")
    private String content;


    private static final long serialVersionUID = 1L;
}
