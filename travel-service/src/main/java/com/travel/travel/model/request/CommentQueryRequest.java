package com.travel.travel.model.request;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
@ApiModel("查询评论请求体")
public class CommentQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    @ApiModelProperty(hidden = true)
    private String searchText;

    /**
     * 评论类型（文章、视频）
     */
    @ApiModelProperty(required = true,value = "评论类型（文章、视频）")
    private Integer commentObjType;

    /**
     * 评论类型下的对象 id
     */
    @ApiModelProperty(required = true,value = "评论类型下的对象 id")
    private Long commentObjId;

    private static final long serialVersionUID = 1L;
}
