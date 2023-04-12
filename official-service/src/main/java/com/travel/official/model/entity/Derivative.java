package com.travel.official.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 周边表
 * @author jianping5
 * @TableName derivative
 */
@TableName(value ="derivative")
@Data
public class Derivative implements Serializable {
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
     * 周边名称
     */
    private String derivativeName;

    /**
     * 封面 URL
     */
    private String coverUrl;

    /**
     * 周边价格
     */
    private Double price;

    /**
     * 周边介绍
     */
    private String intro;

    /**
     * 获取方式（0：现金 1：代币）
     */
    private Integer obtainMethod;

    /**
     * 周边数量
     */
    private Integer totalCount;

    /**
     * 类型 id
     */
    private Integer typeId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 周边获取次数
     */
    private Integer obtainCount;

    /**
     * 周边状态（0：正常 1：异常 2：下架）
     */
    private Integer derivativeState;

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
        Derivative other = (Derivative) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getOfficialId() == null ? other.getOfficialId() == null : this.getOfficialId().equals(other.getOfficialId()))
            && (this.getDerivativeName() == null ? other.getDerivativeName() == null : this.getDerivativeName().equals(other.getDerivativeName()))
            && (this.getCoverUrl() == null ? other.getCoverUrl() == null : this.getCoverUrl().equals(other.getCoverUrl()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getIntro() == null ? other.getIntro() == null : this.getIntro().equals(other.getIntro()))
            && (this.getObtainMethod() == null ? other.getObtainMethod() == null : this.getObtainMethod().equals(other.getObtainMethod()))
            && (this.getTotalCount() == null ? other.getTotalCount() == null : this.getTotalCount().equals(other.getTotalCount()))
            && (this.getTypeId() == null ? other.getTypeId() == null : this.getTypeId().equals(other.getTypeId()))
            && (this.getViewCount() == null ? other.getViewCount() == null : this.getViewCount().equals(other.getViewCount()))
            && (this.getObtainCount() == null ? other.getObtainCount() == null : this.getObtainCount().equals(other.getObtainCount()))
            && (this.getDerivativeState() == null ? other.getDerivativeState() == null : this.getDerivativeState().equals(other.getDerivativeState()))
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
        result = prime * result + ((getDerivativeName() == null) ? 0 : getDerivativeName().hashCode());
        result = prime * result + ((getCoverUrl() == null) ? 0 : getCoverUrl().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getIntro() == null) ? 0 : getIntro().hashCode());
        result = prime * result + ((getObtainMethod() == null) ? 0 : getObtainMethod().hashCode());
        result = prime * result + ((getTotalCount() == null) ? 0 : getTotalCount().hashCode());
        result = prime * result + ((getTypeId() == null) ? 0 : getTypeId().hashCode());
        result = prime * result + ((getViewCount() == null) ? 0 : getViewCount().hashCode());
        result = prime * result + ((getObtainCount() == null) ? 0 : getObtainCount().hashCode());
        result = prime * result + ((getDerivativeState() == null) ? 0 : getDerivativeState().hashCode());
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
        sb.append(", derivativeName=").append(derivativeName);
        sb.append(", coverUrl=").append(coverUrl);
        sb.append(", price=").append(price);
        sb.append(", intro=").append(intro);
        sb.append(", obtainMethod=").append(obtainMethod);
        sb.append(", totalCount=").append(totalCount);
        sb.append(", typeId=").append(typeId);
        sb.append(", viewCount=").append(viewCount);
        sb.append(", obtainCount=").append(obtainCount);
        sb.append(", derivativeState=").append(derivativeState);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}