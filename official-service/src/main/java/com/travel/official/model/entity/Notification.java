package com.travel.official.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 资讯通知表
 * @author jianping5
 * @TableName notification
 */
@TableName(value ="notification")
@Data
public class Notification implements Serializable {
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
     * 所属官方 id
     */
    private Long officialId;

    /**
     * 官方类型 id
     */
    private Integer typeId;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面 URL
     */
    private String coverUrl;

    /**
     * 资讯通知首句话
     */
    private String intro;

    /**
     * 资讯通知详情
     */
    private String detail;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 资源状态（0：正常 1：异常 2：删除）
     */
    private Integer notificationState;

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
        Notification other = (Notification) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getOfficialId() == null ? other.getOfficialId() == null : this.getOfficialId().equals(other.getOfficialId()))
            && (this.getTypeId() == null ? other.getTypeId() == null : this.getTypeId().equals(other.getTypeId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getCoverUrl() == null ? other.getCoverUrl() == null : this.getCoverUrl().equals(other.getCoverUrl()))
            && (this.getIntro() == null ? other.getIntro() == null : this.getIntro().equals(other.getIntro()))
            && (this.getDetail() == null ? other.getDetail() == null : this.getDetail().equals(other.getDetail()))
            && (this.getViewCount() == null ? other.getViewCount() == null : this.getViewCount().equals(other.getViewCount()))
            && (this.getNotificationState() == null ? other.getNotificationState() == null : this.getNotificationState().equals(other.getNotificationState()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getOfficialId() == null) ? 0 : getOfficialId().hashCode());
        result = prime * result + ((getTypeId() == null) ? 0 : getTypeId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getCoverUrl() == null) ? 0 : getCoverUrl().hashCode());
        result = prime * result + ((getIntro() == null) ? 0 : getIntro().hashCode());
        result = prime * result + ((getDetail() == null) ? 0 : getDetail().hashCode());
        result = prime * result + ((getViewCount() == null) ? 0 : getViewCount().hashCode());
        result = prime * result + ((getNotificationState() == null) ? 0 : getNotificationState().hashCode());
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
        sb.append(", officialId=").append(officialId);
        sb.append(", typeId=").append(typeId);
        sb.append(", title=").append(title);
        sb.append(", coverUrl=").append(coverUrl);
        sb.append(", intro=").append(intro);
        sb.append(", detail=").append(detail);
        sb.append(", viewCount=").append(viewCount);
        sb.append(", notificationState=").append(notificationState);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}