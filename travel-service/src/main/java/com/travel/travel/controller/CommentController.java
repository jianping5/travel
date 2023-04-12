package com.travel.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.travel.model.entity.Comment;
import com.travel.travel.model.request.*;
import com.travel.travel.model.vo.CommentVOBlock;
import com.travel.travel.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author zgy
 * @createDate 22/3/2023 下午 3:10
 */
@RestController
@RequestMapping("/travel")
public class CommentController {
    @Resource
    private CommentService commentService;

    @PostMapping("/comment/add")
    public BaseResponse<Long> addComment(@RequestBody CommentAddRequest commentAddRequest) {
        // 校验请求体
        if (commentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 Comment 中
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentAddRequest, comment);

        // 校验 Comment 信息是否合法
        commentService.validComment(comment, true);

        // 添加评论
        Comment newComment = commentService.addComment(comment);

        // 获取评论 id
        long newCommentId = newComment.getId();

        return ResultUtils.success(newCommentId);
    }

    @PostMapping("/comment/delete")
    public BaseResponse<Boolean> deleteComment(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 Comment id
        long id = deleteRequest.getId();

        // 判断是否存在
        Comment oldPost = commentService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        boolean result = commentService.deleteComment(oldPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    @PostMapping("/comment/list/page/vo")
    public BaseResponse<Page<CommentVOBlock>> listCommentVOByPage(@RequestBody CommentQueryRequest commentQueryRequest) {
        if (commentQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long size = commentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(commentService.queryComment(commentQueryRequest));
    }
}
