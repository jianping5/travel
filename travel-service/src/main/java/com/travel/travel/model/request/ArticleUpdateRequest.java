package com.travel.travel.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "更新文章游记请求体")
public class ArticleUpdateRequest implements Serializable {

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
    @ApiModelProperty(value = "所属团队 id")
    private Long teamId;

    /**
     * 权限（0：公开 1：部分可见 2：私密）
     */
    @ApiModelProperty("权限（0：公开 1：部分可见 2：私密）")
    private Integer permission;

    /**
     * 标签
     */
    @ApiModelProperty(required = true,value = "标签")
    private String tag;

    /**
     * 文章首句话
     */
    @ApiModelProperty(required = true,value = "文章首句话")
    private String intro;

    /**
     * 地理位置
     */
    @ApiModelProperty(required = true,value = "地理位置")
    private String location;

    /**
     * 文章详情
     */
    @ApiModelProperty(required = true,value = "文章详情")
    private String detail;

    /**
     * 文章状态（0：正常 1：异常 2：删除）
     */
    @ApiModelProperty("文章状态（0：正常 1：异常 2：删除）")
    private Integer articleState;

    private static final long serialVersionUID = 1L;
}
