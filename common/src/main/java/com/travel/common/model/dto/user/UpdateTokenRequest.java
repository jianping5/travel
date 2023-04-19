package com.travel.common.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 16/4/2023 下午 8:00
 */
@Data
public class UpdateTokenRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 代币数
     */
    private Integer token;

    /**
     * 是否增加
     */
    private Boolean isAdd;

    private static final long serialVersionUID = 1L;
}
