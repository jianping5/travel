package com.travel.common.model.dto.reward;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 13/4/2023 下午 8:15
 */
@Data
public class ConsumeRecordAddRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 消费信息
     */
    private String content;

    /**
     * 代币金额
     */
    private Integer tokenAccount;

    /**
     * 消费关联类型
     */
    private Integer consumeType;

    /**
     * 消费关联对象 id
     */
    private Long consumeId;


    private static final long serialVersionUID = 1L;
}
