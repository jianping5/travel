package com.travel.official.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.constant.TypeConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.official.mapper.ReviewMapper;
import com.travel.official.model.dto.review.ReviewQueryRequest;
import com.travel.official.model.entity.Review;
import com.travel.official.model.vo.ReviewVO;
import com.travel.official.service.ReviewService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【review(点评表)】的数据库操作Service实现
* @createDate 2023-03-24 19:26:42
*/
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review>
    implements ReviewService{

    @Resource
    private InnerUserService innerUserService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void validReview(Review review, boolean add) {
        if (review == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String detail = review.getContent();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(detail), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(detail) && detail.length() > 1500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "点评过长");
        }
    }

    @Override
    public Long addReview(Review review) {
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        // 设置用户 id
        review.setUserId(loginUserId);

        // todo：设置地理位置

        // 添加到数据库中
        boolean saveResult = this.save(review);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);;

        // 返回该点评 id
        return review.getId();
    }

    @Override
    public boolean deleteReview(Review review) {
        // 将点评状态设置为已删除
        review.setIsDeleted(1);
        boolean result = this.updateById(review);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return true;
    }

    @Override
    public Page<ReviewVO> getReviewVOPage(Page<Review> reviewPage) {
        List<Review> reviewList = reviewPage.getRecords();
        Page<ReviewVO> reviewVOPage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        if (CollectionUtils.isEmpty(reviewList)) {
            return reviewVOPage;
        }

        // 根据资讯通知列表获取资讯通知视图体列表
        List<ReviewVO> reviewVOList = getReviewVOList(reviewList);

        reviewVOPage.setRecords(reviewVOList);
        return reviewVOPage;
    }

    @Override
    public QueryWrapper<Review> getQueryWrapper(ReviewQueryRequest reviewQueryRequest) {
        QueryWrapper<Review> queryWrapper = new QueryWrapper<>();
        if (reviewQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询条件中的字段
        String searchText = reviewQueryRequest.getSearchText();
        String sortField = reviewQueryRequest.getSortField();
        String sortOrder = reviewQueryRequest.getSortOrder();
        Long id = reviewQueryRequest.getId();
        String content = reviewQueryRequest.getContent();
        Long userId = reviewQueryRequest.getUserId();
        Integer isDeleted = reviewQueryRequest.getIsDeleted();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        queryWrapper.eq(ObjectUtils.isNotEmpty(isDeleted), "is_deleted", 0);
        return queryWrapper;
    }

    @Override
    public List<ReviewVO> getReviewVOList(List<Review> reviewList) {
        // 获取用户 id 列表
        Set<Long> userIdSet = reviewList.stream().map(Review::getUserId).collect(Collectors.toSet());

        // 获取用户列表
        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);

        // 将用户 id 和用户对应起来
        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));

        // 若已登录，点赞状态
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();

        // 填充信息
        List<ReviewVO> reviewVOList = reviewList.stream().map(review -> {
            // 获取官方资源视图体
            ReviewVO reviewVO = ReviewVO.objToVo(review);

            // 注入用户到资讯通知视图体内
            Long userId = review.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            reviewVO.setUser(userDTO);

            // 是否点赞
            String reviewLike = String.format("travel:official:like:%d:%d", TypeConstant.REVIEW.getTypeIndex(), review.getId());
            RSet<Long> reviewLikeSet = redissonClient.getSet(reviewLike);
            if (reviewLikeSet.contains(loginUserId)) {
                reviewVO.setIsLiked(1);
            } else {
                reviewVO.setIsLiked(0);
            }

            return reviewVO;
        }).collect(Collectors.toList());

        return reviewVOList;
    }
}




