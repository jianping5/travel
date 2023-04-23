package com.travel.travel.model.request;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
@ApiModel(value = "查询文章游记请求体")
public class ArticleQueryRequest extends PageRequest implements Serializable {

    /**
     * 所属用户 id
     */
    @ApiModelProperty(required = false)
    private Long userId;

    /**
     * 文章权限
     */
    @ApiModelProperty(hidden = true)
    private Integer permission;

    /**
     * 所属用户 id
     */
    @ApiModelProperty(hidden = true)
    private Long teamId;

    /**
     * 查询类型（0：旅行游记，1：官方游记，2：旅游攻略）
     */
    private Integer queryType;

    /**
     * 排序类型（0：热门推荐，1：最新发布）
     */
    private Integer orderType;

    /**
     * 搜索词
     */
    @ApiModelProperty(hidden = true)
    private String searchText;

    /**
     * id列表
     */
    private List<Long> idList;

    /**
     * 文章状态
     */
    @ApiModelProperty(hidden = true)
    private Integer articleState;

    private static final long serialVersionUID = 1L;
}
