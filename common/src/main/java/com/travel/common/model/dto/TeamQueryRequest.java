package com.travel.common.model.dto;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
public class TeamQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建人 id
     */
    private Long userId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 团队名
     */
    private String teamName;

    /**
     * 团队介绍
     */
    private String intro;

    /**
     * 是否需要审核（0：不需要 1：需要）
     */
    private Integer isAudit;

    /**
     * 团队状态（0：正常 1：异常 2：已解散）
     */
    private Integer teamState;

    private static final long serialVersionUID = 1L;
}
