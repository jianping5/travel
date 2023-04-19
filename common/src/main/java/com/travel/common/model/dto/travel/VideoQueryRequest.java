package com.travel.common.model.dto.travel;

import com.travel.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class VideoQueryRequest extends PageRequest implements Serializable {

    /**
     * 所属用户 id
     */
    private Long userId;

    /**
     * 视频权限
     */
    private Integer permission;

    /**
     * 所属用户 id
     */
    private Long teamId;
    /**
     * 查询类型（0：旅行游记，1：官方游记，2：旅游攻略）
     */
    private Integer queryType;

    /**
     * 查询类型（0：热门推荐，1：最新发布）
     */
    private Integer orderType;

    /**
     * 上一页最后一条记录的id
     */
    private Long lastEndId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标签
     */
    private String tag;


    /**
     * 视频状态
     */
    private Integer videoState;

    private static final long serialVersionUID = 1L;
}
