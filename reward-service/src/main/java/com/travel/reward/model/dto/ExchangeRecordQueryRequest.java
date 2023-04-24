package com.travel.reward.model.dto;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 兑换记录请求体
 * @author jianping5
 * @createDate 15/4/2023 下午 7:55
 */
@Data
@ApiModel(value = "兑换记录请求体")
public class ExchangeRecordQueryRequest extends PageRequest implements Serializable {

    /**
     * 兑换记录 id
     */
    @ApiModelProperty("兑换记录 id")
    private Long id;

    /**
     * 用户 id（若官方查询则可以不传）
     */
    @ApiModelProperty("用户 id（若官方查询则可以不传）")
    private Long userId;

    /**
     * 周边 id
     */
    @ApiModelProperty("周边 id")
    private Long derivativeId;

    /**
     * 兑换凭证
     */
    @ApiModelProperty("兑换凭证")
    private String certificate;

    /**
     * 搜索词
     */
    @ApiModelProperty("搜索词")
    private String searchText;

    /**
     * 是否为官方（0：否 1：是）
     */
    @ApiModelProperty(value = "是否为官方（0：否 1：是）", required = true)
    private Integer isOfficial;

    /**
     * 是否删除（0：否 1：是）
     */
    @ApiModelProperty("是否删除（0：否 1：是）")
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}
