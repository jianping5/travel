package com.travel.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.travel.model.entity.Comment;
import com.travel.travel.model.request.*;
import com.travel.travel.model.vo.CommentVOBlock;
import com.travel.travel.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author zgy
 * @createDate 22/3/2023 下午 3:10
 */
@RestController
@Api(tags = "Comment 控制器")
@RequestMapping("/travel")
public class CommentController {
    @Resource
    private CommentService commentService;

    @PostMapping("/comment/add")
    @ApiOperation("发布评论")
    public BaseResponse<Long> addComment(@RequestBody CommentAddRequest commentAddRequest) {
        // 校验请求体
        if (commentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 Comment 中
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentAddRequest, comment);
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        // 设置用户 id
        comment.setUserId(loginUserId);

        // 校验 Comment 信息是否合法
        commentService.validComment(comment, true);

        // 添加评论
        Comment newComment = commentService.addComment(comment);

        // 获取评论 id
        long newCommentId = newComment.getId();

        return ResultUtils.success(newCommentId);
    }

    @PostMapping("/comment/delete")
    @ApiOperation("删除评论")
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

        //todo:删除一级评论的二级评论
        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<Comment>().eq("top_id", deleteRequest.getId());
        updateWrapper.set("is_deleted",1);
        boolean remove = commentService.update(updateWrapper);

        return ResultUtils.success(true);
    }

    @PostMapping("/comment/list/page/vo")
    @ApiOperation("获取评论")
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
