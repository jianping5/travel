package com.travel.team.model.dto.news;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 团队动态查询请求体
 * @author jianping5
 * @createDate 23/3/2023 下午 2:06
 */
@Data
@ApiModel(value = "团队动态查询请求体")
public class TeamNewsQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "团队动态 id")
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
     * 动态内容
     */
    @ApiModelProperty(value = "动态内容")
    private String content;

    /**
     * 图片 URL
     */
    @ApiModelProperty(value = "图片 URL")
    private String imageUrl;


    private static final long serialVersionUID = 1L;
}
