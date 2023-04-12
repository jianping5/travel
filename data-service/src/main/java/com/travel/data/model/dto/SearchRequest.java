package com.travel.data.model.dto;

import com.travel.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 * @author jianping5
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 类型
     */
    private String type;

    /**
     * 分类类型 id
     */
    private Integer classifyTypeId;

    private static final long serialVersionUID = 1L;
}