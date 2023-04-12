package com.travel.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.travel.model.entity.Article;
import com.travel.travel.model.entity.ArticleDetail;
import com.travel.travel.model.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.travel.model.request.ArticleQueryRequest;
import com.travel.travel.model.request.CommentQueryRequest;
import com.travel.travel.model.vo.ArticleVO;
import com.travel.travel.model.vo.CommentVOBlock;

import java.util.List;

/**
* @author jianping5
* @description 针对表【comment(评论表)】的数据库操作Service
* @createDate 2023-03-24 19:23:06
*/
public interface CommentService extends IService<Comment> {

    /**
     * 校验 Comment
     * @param comment
     * @param b
     */
    void validComment(Comment comment, boolean b);
    
    /**
     * 分页查询评论
     */
    Page<CommentVOBlock> queryComment(CommentQueryRequest commentQueryRequest);

    /**
     * 发布评论
     */
    Comment addComment(Comment Comment);

    /**
     * 删除评论
     */
    boolean deleteComment(Comment Comment);
    
}
