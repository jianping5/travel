package com.travel.official.model.dto.derivative;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 周边获取请求体
 *
 * @author jianping5
 * @createDate 31/3/2023 下午 6:49
 */
@Data
@ApiModel(value = "周边获取请求体")
public class DerivativeObtainRequest implements Serializable {

    /**
     * 周边 id
     */
    @ApiModelProperty(value = "周边 id", required = true)
    private Long id;


    private static final long serialVersionUID = 1L;
}
