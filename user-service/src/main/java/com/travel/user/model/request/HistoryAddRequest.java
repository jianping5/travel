package com.travel.user.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class HistoryAddRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 浏览记录类型
     */
    private Integer historyObjType;

    /**
     * 浏览对象 id
     */
    private Long historyObjId;


    private static final long serialVersionUID = 1L;

}
