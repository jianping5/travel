package com.travel.user.model.request;

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
@ApiModel(value = "关注请求体")
public class FollowRequest implements Serializable {

    /**
     * 用户 id
     */
    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(required = true,value = "被关注用户 id")
    /**
     * 被关注用户 id
     */
    private Long followUserId;

    @ApiModelProperty(required = true,value = "关注状态（0：关注 1：取消关注）")
    /**
     * 关注状态（0：关注 1：取消关注）
     */
    private Integer followState;

    private static final long serialVersionUID = 1L;

}
