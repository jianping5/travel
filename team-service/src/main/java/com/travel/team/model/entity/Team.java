package com.travel.team.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 团队表
 * @author jianping5
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建人 id
     */
    private Long userId;

    /**
     * 团队名
     */
    private String teamName;

    /**
     * 团队图标 URL
     */
    private String iconUrl;

    /**
     * 团队封面 URL
     */
    private String coverUrl;

    /**
     * 团队介绍
     */
    private String intro;

    /**
     * 团队人数
     */
    private Integer teamSize;

    /**
     * 团队容量
     */
    private Integer capacity;

    /**
     * 团队动态数
     */
    private Integer newsCount;

    /**
     * 团队游记数
     */
    private Integer travelCount;

    /**
     * 是否需要审核（0：不需要 1：需要）
     */
    private Integer isAudit;

    /**
     * 团队状态（0：正常 1：异常 2：已解散）
     */
    private Integer teamState;

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
        Team other = (Team) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getTeamName() == null ? other.getTeamName() == null : this.getTeamName().equals(other.getTeamName()))
            && (this.getIconUrl() == null ? other.getIconUrl() == null : this.getIconUrl().equals(other.getIconUrl()))
            && (this.getCoverUrl() == null ? other.getCoverUrl() == null : this.getCoverUrl().equals(other.getCoverUrl()))
            && (this.getIntro() == null ? other.getIntro() == null : this.getIntro().equals(other.getIntro()))
            && (this.getTeamSize() == null ? other.getTeamSize() == null : this.getTeamSize().equals(other.getTeamSize()))
            && (this.getCapacity() == null ? other.getCapacity() == null : this.getCapacity().equals(other.getCapacity()))
            && (this.getNewsCount() == null ? other.getNewsCount() == null : this.getNewsCount().equals(other.getNewsCount()))
            && (this.getTravelCount() == null ? other.getTravelCount() == null : this.getTravelCount().equals(other.getTravelCount()))
            && (this.getIsAudit() == null ? other.getIsAudit() == null : this.getIsAudit().equals(other.getIsAudit()))
            && (this.getTeamState() == null ? other.getTeamState() == null : this.getTeamState().equals(other.getTeamState()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTeamName() == null) ? 0 : getTeamName().hashCode());
        result = prime * result + ((getIconUrl() == null) ? 0 : getIconUrl().hashCode());
        result = prime * result + ((getCoverUrl() == null) ? 0 : getCoverUrl().hashCode());
        result = prime * result + ((getIntro() == null) ? 0 : getIntro().hashCode());
        result = prime * result + ((getTeamSize() == null) ? 0 : getTeamSize().hashCode());
        result = prime * result + ((getCapacity() == null) ? 0 : getCapacity().hashCode());
        result = prime * result + ((getNewsCount() == null) ? 0 : getNewsCount().hashCode());
        result = prime * result + ((getTravelCount() == null) ? 0 : getTravelCount().hashCode());
        result = prime * result + ((getIsAudit() == null) ? 0 : getIsAudit().hashCode());
        result = prime * result + ((getTeamState() == null) ? 0 : getTeamState().hashCode());
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
        sb.append(", teamName=").append(teamName);
        sb.append(", iconUrl=").append(iconUrl);
        sb.append(", coverUrl=").append(coverUrl);
        sb.append(", intro=").append(intro);
        sb.append(", teamSize=").append(teamSize);
        sb.append(", capacity=").append(capacity);
        sb.append(", newsCount=").append(newsCount);
        sb.append(", travelCount=").append(travelCount);
        sb.append(", isAudit=").append(isAudit);
        sb.append(", teamState=").append(teamState);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}