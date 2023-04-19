package com.travel.reward.model.dto;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 15/4/2023 下午 7:55
 */
@Data
public class ExchangeRecordQueryRequest extends PageRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 周边 id
     */
    private Long derivativeId;

    /**
     * 兑换凭证
     */
    private String certificate;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 是否为官方
     */
    private Integer isOfficial;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}
