package com.travel.user.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "点赞请求体")
public class UserLikeRequest implements Serializable {
    /**
     * 用户 id
     */
    @ApiModelProperty(hidden = true)
    private Long userId;

    /**
     * 点赞类型
     */
    @ApiModelProperty(value = "点赞类型")
    private Integer likeObjType;

    /**
     * 点赞对象 id
     */
    @ApiModelProperty(value = "点赞对象 id")
    private Long likeObjId;

    /**
     * 点赞状态（0：点赞 1：取消点赞）
     */
    @ApiModelProperty(value = "点赞状态（0：点赞 1：取消点赞）")
    private Integer likeState;


    private static final long serialVersionUID = 1L;

}
