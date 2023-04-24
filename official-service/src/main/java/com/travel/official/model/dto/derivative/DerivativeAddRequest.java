package com.travel.official.model.dto.derivative;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 周边添加请求体
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "周边添加请求体")
public class DerivativeAddRequest implements Serializable {

    /**
     * 周边 id
     */
    @ApiModelProperty(value = "周边 id")
    private Long id;

    /**
     * 所属用户 id
     */
    @ApiModelProperty(value = "所属用户 id")
    private Long userId;

    /**
     * 所属官方 id
     */
    @ApiModelProperty(value = "所属官方 id")
    private Long officialId;

    /**
     * 周边名称
     */
    @ApiModelProperty(value = "周边名称", required = true)
    private String derivativeName;

    /**
     * 封面 URL
     */
    @ApiModelProperty(value = "封面 URL", required = true)
    private String coverUrl;

    /**
     * 周边价格（要么是代币，要么是价格）
     */
    @ApiModelProperty(value = "周边价格（要么是代币，要么是价格）", required = true)
    private Double price;

    /**
     * 周边介绍
     */
    @ApiModelProperty(value = "周边介绍", required = true)
    private String intro;

    /**
     * 获取方式（0：现金 1：代币）
     */
    @ApiModelProperty(value = "获取方式（0：现金 1：代币）", required = true)
    private Integer obtainMethod;

    /**
     * 周边数量
     */
    @ApiModelProperty(value = "周边数量")
    private Integer totalCount;

    /**
     * 类型 id（1：景区，2：酒店，3：美食）
     */
    @ApiModelProperty(value = "类型 id（1：景区，2：酒店，3：美食）", required = true)
    private Integer typeId;

    private static final long serialVersionUID = 1L;

}
