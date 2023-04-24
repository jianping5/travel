package com.travel.common.model.dto.travel;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 游记推荐请求体
 * @author jianping5
 * @createDate 11/4/2023 下午 8:21
 */
@Data
@ApiModel(value = "游记推荐请求体")
public class TravelRcmdRequest extends PageRequest implements Serializable {

    /**
     * 推荐类型（景区的相关游记、游记的相关游记）
     * 三种 Official、Article、Video
     */
    @ApiModelProperty(value = "推荐类型（景区的相关游记、游记的相关游记）三种 Official、Article、Video", required = true)
    private Integer rcmdType;

    /**
     * 游记类型
     */
    @ApiModelProperty(value = "游记类型（文章 or 视频）", required = true)
    private Integer travelType;

    /**
     * todo：用户 id（是否需要？）
     */
    @ApiModelProperty(value = "用户 id（是否需要？）")
    private Long userId;

    /**
     * 标签
     */
    @ApiModelProperty(value = "标签", required = true)
    private String tag;

    /**
     * 相关 id
     */
    @ApiModelProperty(value = "相关 id")
    private Long id;

    private static final long serialVersionUID = 1L;

}
