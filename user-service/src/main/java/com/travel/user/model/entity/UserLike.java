package com.travel.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 点赞表
 * @TableName user_like
 */
@TableName(value ="user_like")
@Data
public class UserLike implements Serializable {
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
     * 点赞类型
     */
    private Integer likeObjType;

    /**
     * 点赞对象 id
     */
    private Long likeObjId;

    /**
     * 点赞状态（0：点赞 1：取消点赞）
     */
    private Integer likeState;

    /**
     * 点赞时间
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
        UserLike other = (UserLike) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getLikeObjType() == null ? other.getLikeObjType() == null : this.getLikeObjType().equals(other.getLikeObjType()))
            && (this.getLikeObjId() == null ? other.getLikeObjId() == null : this.getLikeObjId().equals(other.getLikeObjId()))
            && (this.getLikeState() == null ? other.getLikeState() == null : this.getLikeState().equals(other.getLikeState()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getLikeObjType() == null) ? 0 : getLikeObjType().hashCode());
        result = prime * result + ((getLikeObjId() == null) ? 0 : getLikeObjId().hashCode());
        result = prime * result + ((getLikeState() == null) ? 0 : getLikeState().hashCode());
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
        sb.append(", likeObjType=").append(likeObjType);
        sb.append(", likeObjId=").append(likeObjId);
        sb.append(", likeState=").append(likeState);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}