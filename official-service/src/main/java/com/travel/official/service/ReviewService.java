package com.travel.official.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.official.model.dto.review.ReviewQueryRequest;
import com.travel.official.model.entity.Review;
import com.travel.official.model.vo.ReviewVO;

import java.util.List;

/**
* @author jianping5
* @description 针对表【review(点评表)】的数据库操作Service
* @createDate 2023-03-24 19:26:42
*/
public interface ReviewService extends IService<Review> {
    /**
     * 校验 官方点评
     * @param review
     * @param b
     */
    void validReview(Review review, boolean b);

    /**
     * 添加官方点评
     * @param review
     * @return
     */
    Long addReview(Review review);


    /**
     * 下架官方点评
     * @param review
     * @return
     */
    boolean deleteReview(Review review);
    
    /**
     * 获取分页官方视图体
     * @param reviewPage
     * @return
     */
    Page<ReviewVO> getReviewVOPage(Page<Review> reviewPage);

    /**
     * 根据请求体获取请求 Wrapper
     * @param reviewQueryRequest
     * @return
     */
    QueryWrapper<Review> getQueryWrapper(ReviewQueryRequest reviewQueryRequest);

    /**
     * 根据官方点评列表获取官方点评视图体列表
     * @param reviewList
     * @return
     */
    List<ReviewVO> getReviewVOList(List<Review> reviewList);
}
