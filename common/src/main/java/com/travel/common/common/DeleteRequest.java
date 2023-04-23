package com.travel.common.common;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author jianping5
 */
@Data
@ApiModel(value = "删除请求体")
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}