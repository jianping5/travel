package com.travel.official.model.dto.officialResource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 官方资源更新请求体
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "官方资源更新请求体")
public class OfficialResourceUpdateRequest implements Serializable {

    /**
     * 官方 id
     */
    @ApiModelProperty(value = "官方资源 id", required = true)
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
     * 封面 URL
     */
    @ApiModelProperty(value = "封面 URL", required = true)
    private String coverUrl;

    /**
     * 价格（仅显示）
     */
    @ApiModelProperty(value = "价格（仅显示）")
    private String price;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题", required = true)
    private String title;

    /**
     * 类型 id
     */
    @ApiModelProperty(value = "类型 id")
    private Integer typeId;

    /**
     * 官方资源首句话
     */
    @ApiModelProperty(value = "官方资源首句话", required = true)
    private String intro;

    /**
     * 官方资源详情 id
     */
    @ApiModelProperty(value = "官方资源详情 id", required = true)
    private Long resourceDetailId;

    /**
     * 资源状态（0：正常 1：异常 2：下架）
     */
    private Integer resourceState;

    /**
     * 官方资源详情
     */
    @ApiModelProperty(value = "官方资源详情", required = true)
    private String detail;

    private static final long serialVersionUID = 1L;
}
