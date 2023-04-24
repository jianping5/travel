package com.travel.travel.model.request;

import com.travel.common.common.PageRequest;
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
@ApiModel(value = "查询视频游记请求体")
public class VideoQueryRequest extends PageRequest implements Serializable {

    /**
     * 所属用户 id
     */
    @ApiModelProperty(hidden = true)
    private Long userId;

    /**
     * 视频权限
     */
    @ApiModelProperty(hidden = true)
    private Integer permission;

    @ApiModelProperty(hidden = true)
    private Long teamId;

    /**
     * 查询类型（0：旅行游记，1：官方游记，2：旅游攻略）
     */
    @ApiModelProperty(value = "查询类型（0：旅行游记，1：官方游记，2：旅游攻略）")
    private Integer queryType;


    /**
     * 查询类型（0：热门推荐，1：最新发布）
     */
    @ApiModelProperty("查询类型（0：热门推荐，1：最新发布）")
    private Integer orderType;

    @ApiModelProperty(hidden = true)
    /**
     * 搜索词
     */
    private String searchText;

    /**
     * id列表
     */
    @ApiModelProperty("id列表")
    private List<Long> idList;

    @ApiModelProperty(hidden = true)
    /**
     * 视频状态
     */
    private Integer videoState;

    private static final long serialVersionUID = 1L;
}
