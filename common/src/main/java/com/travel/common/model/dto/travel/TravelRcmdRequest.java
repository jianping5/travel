package com.travel.common.model.dto.travel;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 11/4/2023 下午 8:21
 */
@Data
public class TravelRcmdRequest extends PageRequest implements Serializable {

    /**
     * 推荐类型（景区的相关游记、游记的相关游记）
     * 三种 Official、Article、Video
     */
    private Integer rcmdType;

    /**
     * 游记类型
     */
    private Integer travelType;

    /**
     * todo：用户 id（是否需要？）
     */
    private Long userId;

    /**
     * 标签
     */
    private String tag;

    /**
     * 相关 id
     */
    private Long id;

    private static final long serialVersionUID = 1L;

}
