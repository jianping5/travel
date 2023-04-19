package com.travel.reward.model.dto;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 15/4/2023 下午 7:55
 */
@Data
public class ConsumeRecordQueryRequest extends PageRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 消费关联类型
     */
    private Integer consumeType;

    /**
     * 消费关联对象 id
     */
    private Long consumeId;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    /**
     * 搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
}
