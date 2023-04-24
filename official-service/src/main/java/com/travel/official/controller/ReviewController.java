package com.travel.official.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.official.model.dto.review.ReviewAddRequest;
import com.travel.official.model.dto.review.ReviewQueryRequest;
import com.travel.official.model.entity.Review;
import com.travel.official.model.vo.ReviewVO;
import com.travel.official.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 点评 Controller
 * @author jianping5
 * @createDate 6/4/2023 下午 8:25
 */
@RestController
@RequestMapping("/review")
@Api(tags = "点评 Controller")
public class ReviewController {
    
    @Resource
    private ReviewService reviewService;
    
    /**
     * 添加点评
     *
     * @param reviewAddRequest
     * @return
     */
    @ApiOperation(value = "添加点评")
    @PostMapping("/add")
    public BaseResponse<Long> addReview(@RequestBody ReviewAddRequest reviewAddRequest) {
        // 校验请求体
        if (reviewAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 Review 中
        Review review = new Review();
        BeanUtils.copyProperties(reviewAddRequest, review);

        // 校验 Review 信息是否合法
        reviewService.validReview(review, true);

        // 添加点评，并返回 id
        return ResultUtils.success(reviewService.addReview(review));
    }

    /**
     * 删除点评
     *
     * @param deleteRequest
     * @return
     */
    @ApiOperation(value = "删除点评")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteReview(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 Review id
        long id = deleteRequest.getId();

        // 判断是否存在
        Review oldReview = reviewService.getById(id);
        ThrowUtils.throwIf(oldReview == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!oldReview.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(2)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = reviewService.deleteReview(oldReview);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param reviewQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取列表（封装类）")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ReviewVO>> listReviewVOByPage(@RequestBody ReviewQueryRequest reviewQueryRequest) {
        if (reviewQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = reviewQueryRequest.getCurrent();
        long size = reviewQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Review> reviewPage = reviewService.page(new Page<>(current, size),
                reviewService.getQueryWrapper(reviewQueryRequest));

        return ResultUtils.success(reviewService.getReviewVOPage(reviewPage));
    }
}
