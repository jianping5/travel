package com.travel.travel.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 视频表
 * @TableName video
 */
@TableName(value ="video")
@Data
public class Video implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户 id
     */
    private Long userId;

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
     * 文章状态（0：正常 1：异常 2：删除）
     */
    private Integer videoState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Video other = (Video) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getCoverUrl() == null ? other.getCoverUrl() == null : this.getCoverUrl().equals(other.getCoverUrl()))
            && (this.getTeamId() == null ? other.getTeamId() == null : this.getTeamId().equals(other.getTeamId()))
            && (this.getIntro() == null ? other.getIntro() == null : this.getIntro().equals(other.getIntro()))
            && (this.getVideoUrl() == null ? other.getVideoUrl() == null : this.getVideoUrl().equals(other.getVideoUrl()))
            && (this.getPermission() == null ? other.getPermission() == null : this.getPermission().equals(other.getPermission()))
            && (this.getTag() == null ? other.getTag() == null : this.getTag().equals(other.getTag()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
            && (this.getCommentCount() == null ? other.getCommentCount() == null : this.getCommentCount().equals(other.getCommentCount()))
            && (this.getFavoriteCount() == null ? other.getFavoriteCount() == null : this.getFavoriteCount().equals(other.getFavoriteCount()))
            && (this.getViewCount() == null ? other.getViewCount() == null : this.getViewCount().equals(other.getViewCount()))
            && (this.getVideoState() == null ? other.getVideoState() == null : this.getVideoState().equals(other.getVideoState()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getCoverUrl() == null) ? 0 : getCoverUrl().hashCode());
        result = prime * result + ((getTeamId() == null) ? 0 : getTeamId().hashCode());
        result = prime * result + ((getIntro() == null) ? 0 : getIntro().hashCode());
        result = prime * result + ((getVideoUrl() == null) ? 0 : getVideoUrl().hashCode());
        result = prime * result + ((getPermission() == null) ? 0 : getPermission().hashCode());
        result = prime * result + ((getTag() == null) ? 0 : getTag().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
        result = prime * result + ((getCommentCount() == null) ? 0 : getCommentCount().hashCode());
        result = prime * result + ((getFavoriteCount() == null) ? 0 : getFavoriteCount().hashCode());
        result = prime * result + ((getViewCount() == null) ? 0 : getViewCount().hashCode());
        result = prime * result + ((getVideoState() == null) ? 0 : getVideoState().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", title=").append(title);
        sb.append(", coverUrl=").append(coverUrl);
        sb.append(", teamId=").append(teamId);
        sb.append(", intro=").append(intro);
        sb.append(", videoUrl=").append(videoUrl);
        sb.append(", permission=").append(permission);
        sb.append(", tag=").append(tag);
        sb.append(", location=").append(location);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", commentCount=").append(commentCount);
        sb.append(", favoriteCount=").append(favoriteCount);
        sb.append(", viewCount=").append(viewCount);
        sb.append(", videoState=").append(videoState);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}