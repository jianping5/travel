package com.travel.travel.model.request;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
public class CommentQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 评论类型（文章、视频）
     */
    private Integer commentObjType;
    /**
     * 评论类型下的对象 id
     */
    private Long commentObjId;

    private static final long serialVersionUID = 1L;
}
