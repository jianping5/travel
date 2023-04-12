package com.travel.data.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户行为表
 * @TableName behavior
 */
@TableName(value ="behavior")
@Data
public class Behavior implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 行为对象类型
     */
    private Integer behaviorObjType;

    /**
     * 行为对象 id
     */
    private Long behaviorObjId;

    /**
     * 行为类型
     */
    private Integer behaviorType;

    /**
     * 访问次数
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
        Behavior other = (Behavior) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getBehaviorObjType() == null ? other.getBehaviorObjType() == null : this.getBehaviorObjType().equals(other.getBehaviorObjType()))
            && (this.getBehaviorObjId() == null ? other.getBehaviorObjId() == null : this.getBehaviorObjId().equals(other.getBehaviorObjId()))
            && (this.getBehaviorType() == null ? other.getBehaviorType() == null : this.getBehaviorType().equals(other.getBehaviorType()))
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
        result = prime * result + ((getBehaviorObjType() == null) ? 0 : getBehaviorObjType().hashCode());
        result = prime * result + ((getBehaviorObjId() == null) ? 0 : getBehaviorObjId().hashCode());
        result = prime * result + ((getBehaviorType() == null) ? 0 : getBehaviorType().hashCode());
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
        sb.append(", behaviorObjType=").append(behaviorObjType);
        sb.append(", behaviorObjId=").append(behaviorObjId);
        sb.append(", behaviorType=").append(behaviorType);
        sb.append(", viewCount=").append(viewCount);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}