package com.travel.user.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.travel.user.model.entity.Follow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zgy
 * @create 2023-04-12 16:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowVO implements Serializable {
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
     * 被关注用户头像
     */
    private String followUserAvatar;

    /**
     * 被关注用户昵称
     */
    private String followUserName;

    private static final long serialVersionUID = 1L;
    public static FollowVO objToVo(Follow follow){
        if(follow == null){
            return null;
        }
        FollowVO followVO = new FollowVO();
        BeanUtil.copyProperties(follow,followVO);
        return followVO;
    }

}
