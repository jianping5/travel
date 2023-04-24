package com.travel.official.model.dto.officialApply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 官方申请添加请求体
 * @author jianping5
 * @createDate 3/4/2023 下午 7:00
 */
@Data
@ApiModel(value = "官方申请添加请求体")
public class OfficialApplyAddRequest implements Serializable {

    /**
     * 官方申请 id
     */
    @ApiModelProperty(value = "官方申请 id")
    private Long id;

    /**
     * 所属用户 id
     */
    @ApiModelProperty(value = "所属用户 id")
    private Long userId;

    /**
     * 官方名
     */
    @ApiModelProperty(value = "官方名", required = true)
    private String officialName;

    /**
     * 省份
     */
    @ApiModelProperty(value = "省份", required = true)
    private String province;

    /**
     * 城市
     */
    @ApiModelProperty(value = "城市", required = true)
    private String city;

    /**
     * 地点
     */
    @ApiModelProperty(value = "地点", required = true)
    private String location;

    /**
     * 类型 id（景区：1，酒店：2，美食：3）暂时写死
     */
    @ApiModelProperty(value = "类型 id（景区：1，酒店：2，美食：3）暂时写死", required = true)
    private Integer typeId;

    /**
     * 联系方式
     */
    @ApiModelProperty(value = "联系方式", required = true)
    private String contact;

    /**
     * 佐证材料（文件上传后的 url）
     */
    @ApiModelProperty(value = "佐证材料（文件上传后的 url）", required = true)
    private String material;

    /**
     * 申请状态（0：待审批 1：已同意 2：已拒绝）
     */
    @ApiModelProperty(value = "申请状态（0：待审批 1：已同意 2：已拒绝）")
    private Integer applyState;

    private static final long serialVersionUID = 1L;
}
