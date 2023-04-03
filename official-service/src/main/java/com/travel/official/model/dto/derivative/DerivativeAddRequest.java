package com.travel.official.model.dto.derivative;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class DerivativeAddRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;

}
