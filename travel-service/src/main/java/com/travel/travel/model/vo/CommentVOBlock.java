package com.travel.travel.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zgy
 * @create 2023-04-10 15:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVOBlock implements Serializable {
    /**
     * 一级评论
     */
    private CommentVO topCommentVO;
    /**
     * 二级评论
     */
    private List<CommentVO> commentVOS;

    private static final long serialVersionUID = 1L;
}
