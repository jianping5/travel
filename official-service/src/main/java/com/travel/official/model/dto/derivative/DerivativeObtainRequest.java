package com.travel.official.model.dto.derivative;

import lombok.Data;

import java.io.Serializable;

/**
 * 周边获取请求体
 *
 * @author jianping5
 * @createDate 31/3/2023 下午 6:49
 */
@Data
public class DerivativeObtainRequest implements Serializable {

    /**
     * 周边 id
     */
    private Long id;

    /**
     * 获取方式
     */
    private Integer obtainMethod;


    private static final long serialVersionUID = 1L;
}
