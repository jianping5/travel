package com.travel.user.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class FollowRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 被关注用户 id
     */
    private Long followUserId;

    /**
     * 关注状态（0：关注 1：取消关注）
     */
    private Integer followState;

    private static final long serialVersionUID = 1L;

}
