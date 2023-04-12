package com.travel.official.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 官方表
 * @author jianping5
 * @TableName official
 */
@TableName(value ="official")
@Data
public class Official implements Serializable {
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
     * 官方名
     */
    private String officialName;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 地点
     */
    private String location;

    /**
     * 经纬度
     */
    private String latAndLong;

    /**
     * 封面 URL
     */
    private String coverUrl;

    /**
     * 视频 URL
     */
    private String videoUrl;

    /**
     * 类型 id
     */
    private Integer typeId;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 标签
     */
    private String tag;

    /**
     * 官方首句话
     */
    private String intro;

    /**
     * 点赞量
     */
    private Integer likeCount;

    /**
     * 点评量
     */
    private Integer reviewCount;

    /**
     * 收藏量
     */
    private Integer favoriteCount;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 官方详情 id
     */
    @TableField(exist = false)
    private Long officialDetailId;

    /**
     * 官方详情
     */
    @TableField(exist = false)
    private String detail;

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
        Official other = (Official) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getOfficialName() == null ? other.getOfficialName() == null : this.getOfficialName().equals(other.getOfficialName()))
            && (this.getProvince() == null ? other.getProvince() == null : this.getProvince().equals(other.getProvince()))
            && (this.getCity() == null ? other.getCity() == null : this.getCity().equals(other.getCity()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getLatAndLong() == null ? other.getLatAndLong() == null : this.getLatAndLong().equals(other.getLatAndLong()))
            && (this.getCoverUrl() == null ? other.getCoverUrl() == null : this.getCoverUrl().equals(other.getCoverUrl()))
            && (this.getVideoUrl() == null ? other.getVideoUrl() == null : this.getVideoUrl().equals(other.getVideoUrl()))
            && (this.getTypeId() == null ? other.getTypeId() == null : this.getTypeId().equals(other.getTypeId()))
            && (this.getContact() == null ? other.getContact() == null : this.getContact().equals(other.getContact()))
            && (this.getTag() == null ? other.getTag() == null : this.getTag().equals(other.getTag()))
            && (this.getIntro() == null ? other.getIntro() == null : this.getIntro().equals(other.getIntro()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
            && (this.getReviewCount() == null ? other.getReviewCount() == null : this.getReviewCount().equals(other.getReviewCount()))
            && (this.getFavoriteCount() == null ? other.getFavoriteCount() == null : this.getFavoriteCount().equals(other.getFavoriteCount()))
            && (this.getViewCount() == null ? other.getViewCount() == null : this.getViewCount().equals(other.getViewCount()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getOfficialName() == null) ? 0 : getOfficialName().hashCode());
        result = prime * result + ((getProvince() == null) ? 0 : getProvince().hashCode());
        result = prime * result + ((getCity() == null) ? 0 : getCity().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getLatAndLong() == null) ? 0 : getLatAndLong().hashCode());
        result = prime * result + ((getCoverUrl() == null) ? 0 : getCoverUrl().hashCode());
        result = prime * result + ((getVideoUrl() == null) ? 0 : getVideoUrl().hashCode());
        result = prime * result + ((getTypeId() == null) ? 0 : getTypeId().hashCode());
        result = prime * result + ((getContact() == null) ? 0 : getContact().hashCode());
        result = prime * result + ((getTag() == null) ? 0 : getTag().hashCode());
        result = prime * result + ((getIntro() == null) ? 0 : getIntro().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
        result = prime * result + ((getReviewCount() == null) ? 0 : getReviewCount().hashCode());
        result = prime * result + ((getFavoriteCount() == null) ? 0 : getFavoriteCount().hashCode());
        result = prime * result + ((getViewCount() == null) ? 0 : getViewCount().hashCode());
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
        sb.append(", officialName=").append(officialName);
        sb.append(", province=").append(province);
        sb.append(", city=").append(city);
        sb.append(", location=").append(location);
        sb.append(", latAndLong=").append(latAndLong);
        sb.append(", coverUrl=").append(coverUrl);
        sb.append(", videoUrl=").append(videoUrl);
        sb.append(", typeId=").append(typeId);
        sb.append(", contact=").append(contact);
        sb.append(", tag=").append(tag);
        sb.append(", intro=").append(intro);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", reviewCount=").append(reviewCount);
        sb.append(", favoriteCount=").append(favoriteCount);
        sb.append(", viewCount=").append(viewCount);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}