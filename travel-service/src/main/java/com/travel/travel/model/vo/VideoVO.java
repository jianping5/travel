package com.travel.travel.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.travel.travel.model.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @author zgy
 * @create 2023-04-03 22:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoVO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 所属用户 id
     */
    private Long userId;

    /**
     * 头像 URL
     */
    private String userAvatar;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面 URL
     */
    private String coverUrl;

    /**
     * 所属团队 id
     */
    private Long teamId;

    /**
     * 介绍
     */
    private String intro;

    /**
     * 视频 URL
     */
    private String videoUrl;

    /**
     * 权限（0：公开 1：部分可见 2：私密）
     */
    private Integer permission;

    /**
     * 标签
     */
    private String tag;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 点赞量
     */
    private Integer likeCount;

    /**
     * 评论量
     */
    private Integer commentCount;

    /**
     * 收藏量
     */
    private Integer favoriteCount;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 是否已点赞
     */
    private Integer isLiked;

    /**
     * 是否已收藏
     */
    private Integer isCollected;

    /**
     * 更新时间
     */
    private Date updateTime;

    public static VideoVO objToVo(Video video){
        if(video == null){
            return null;
        }
        VideoVO videoVO = new VideoVO();
        BeanUtil.copyProperties(video,videoVO);
        return videoVO;
    }
}
