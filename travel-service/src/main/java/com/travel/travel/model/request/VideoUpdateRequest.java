package com.travel.travel.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "更新视频游记请求体")
public class VideoUpdateRequest implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(required = true,value = "主键")
    private Long id;

    /**
     * 所属用户 id
     */
    @ApiModelProperty(required = true,value = "所属用户 id")
    private Long userId;

    /**
     * 标题
     */
    @ApiModelProperty(required = true,value = "标题")
    private String title;

    /**
     * 封面 URL
     */
    @ApiModelProperty(required = true,value = "封面 URL")
    private String coverUrl;

    /**
     * 所属团队 id
     */
    @ApiModelProperty("所属团队 id")
    private Long teamId;

    /**
     * 介绍
     */
    @ApiModelProperty(required = true,value = "介绍")
    private String intro;

    /**
     * 视频 URL
     */

    @ApiModelProperty(required = true,value = "视频 URL")
    private String videoUrl;

    /**
     * 权限（0：公开 1：部分可见 2：私密）
     */
    @ApiModelProperty(value = "权限（0：公开 1：部分可见 2：私密）")
    private Integer permission;

    /**
     * 标签
     */
    @ApiModelProperty(required = true,value = "标签")
    private String tag;

    /**
     * 地理位置
     */
    @ApiModelProperty(required = true,value = "地理位置")
    private String location;

    /**
     * 文章状态（0：正常 1：异常 2：删除）
     */
    @ApiModelProperty(hidden = true,value = "文章状态（0：正常 1：异常 2：删除）")
    private Integer videoState;

    private static final long serialVersionUID = 1L;
}
