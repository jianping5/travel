package com.travel.common.model.dto.official;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 周边表
 * @author jianping5
 * @TableName derivative
 */
@Data
public class DerivativeDTO implements Serializable {
    /**
     * 主键
     */
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

    private static final long serialVersionUID = 1L;

}