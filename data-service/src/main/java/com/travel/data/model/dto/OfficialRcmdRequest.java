package com.travel.data.model.dto;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 11/4/2023 下午 8:21
 */
@Data
public class OfficialRcmdRequest extends PageRequest implements Serializable {

    /**
     * 推荐类型（酒店、美食）
     */
    private Integer rcmdType;

    /**
     * 官方景区 id
     */
    private Long officialId;

    /**
     * 经纬度
     */
    private String latAndLong;

    private static final long serialVersionUID = 1L;

}
