package com.travel.data.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 13/4/2023 下午 7:02
 */
@Data
public class TagAddRequest implements Serializable {

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 标签类型
     */
    private Integer tagType;

    /**
     * 是否为自定义的
     */
    private Integer isCustomized;


    private static final long serialVersionUID = 1L;
}
