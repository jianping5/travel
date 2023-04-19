package com.travel.official.model.dto.official;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 点赞官方请求体
 * @author jianping5
 * @createDate 11/4/2023 下午 7:35
 */
@Data
@ApiModel(value = "点赞官方请求体")
public class OfficialLikeRequest implements Serializable {

    /**
     * 点赞对象 id
     */
    @ApiModelProperty(value = "点赞对象 id")
    private Long id;

    /**
     * 点赞对象类型
     */
    @ApiModelProperty(value = "点赞对象类型")
    private Integer type;

    /**
     * 点赞状态（点赞：0，取消点赞：1）
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}
