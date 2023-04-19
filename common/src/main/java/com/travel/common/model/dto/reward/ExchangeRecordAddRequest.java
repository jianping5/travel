package com.travel.common.model.dto.reward;

import lombok.Data;

import java.io.Serializable;

/**
 * 兑换记录表
 * @author jianping5
 * @TableName exchange_record
 */
@Data
public class ExchangeRecordAddRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 周边 id
     */
    private Long derivativeId;

    /**
     * 代币金额
     */
    private Integer tokenAccount;

    /**
     * 兑换凭证
     */
    private String certificate;

    private static final long serialVersionUID = 1L;
}