package com.travel.common.model.vo;

import com.travel.common.model.dto.travel.VideoDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;


/**
 * @author zgy
 * @create 2023-04-03 22:21
 */
@Data
public class VideoVDTO implements Serializable {

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
     * 发布时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param videoVDTO
     * @return
     */
    public static VideoDTO voToObj(VideoVDTO videoVDTO) {
        if (videoVDTO == null) {
            return null;
        }
        VideoDTO videoDTO = new VideoDTO();
        BeanUtils.copyProperties(videoVDTO, videoDTO);
        return videoDTO;
    }

    /**
     * 对象转包装类
     *
     * @param videoDTO
     * @return
     */
    public static VideoVDTO objToVo(VideoDTO videoDTO) {
        if (videoDTO == null) {
            return null;
        }
        VideoVDTO videoVDTO = new VideoVDTO();
        BeanUtils.copyProperties(videoDTO, videoVDTO);

        return videoVDTO;
    }
}
