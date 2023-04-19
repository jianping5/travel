package com.travel.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 13/4/2023 下午 7:19
 */
@Data
public class TagAddRequest implements Serializable {

    /**
     * 标签数组
     */
    String tagList;

    /**
     * 标签类型
     */
    Integer tagType;

    private static final long serialVersionUID = 1L;

}
