package com.travel.data.model.dto;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 官方推荐请求体（推荐酒店美食）
 * @author jianping5
 * @createDate 11/4/2023 下午 8:21
 */
@Data
@ApiModel(value = "官方推荐请求体（推荐酒店美食）")
public class OfficialRcmdRequest extends PageRequest implements Serializable {

    /**
     * 推荐类型（2：酒店、3：美食）
     */
    @ApiModelProperty(value = "推荐类型（酒店、美食）", required = true)
    private Integer rcmdType;

    /**
     * 官方景区 id
     */
    @ApiModelProperty(value = "官方景区 id", required = true)
    private Long officialId;

    /**
     * 经纬度
     */
    @ApiModelProperty(value = "经纬度", required = true)
    private String latAndLong;

    private static final long serialVersionUID = 1L;

}
