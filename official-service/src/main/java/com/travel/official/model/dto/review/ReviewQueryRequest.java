package com.travel.official.model.dto.review;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
public class ReviewQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 点评类型
     */
    private Integer reviewObjType;

    /**
     * 点评对象
     */
    private Long reviewObjId;

    /**
     * 点评内容
     */
    private String content;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}
