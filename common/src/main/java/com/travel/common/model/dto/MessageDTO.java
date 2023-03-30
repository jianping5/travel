package com.travel.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息表
 * @TableName message
 */
@Data
public class MessageDTO implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息关联用户 id
     */
    private Long messageUserId;

    /**
     * 消息关联类型
     */
    private Integer messageObjType;

    /**
     * 消息关联对象 id
     */
    private Long messageObjId;

    private static final long serialVersionUID = 1L;

}