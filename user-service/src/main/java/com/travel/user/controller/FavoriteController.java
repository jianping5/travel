package com.travel.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.user.model.entity.Favorite;
import com.travel.user.model.request.FavoriteAddRequest;
import com.travel.user.model.request.FavoriteQueryRequest;
import com.travel.user.service.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zgy
 * @createDate 22/3/2023 下午 3:10
 */
@RestController
@Api(tags = "收藏夹 Controller")
@RequestMapping("/favorite")
public class FavoriteController {
    @Resource
    private FavoriteService favoriteService;

    @ApiOperation(value = "创建收藏夹")
    @PostMapping("/add")
    public BaseResponse<Long> addFavorite(@RequestBody FavoriteAddRequest favoriteAddRequest) {
        // 校验请求体
        if (favoriteAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 Favorite 中
        Favorite favorite = new Favorite();
        BeanUtils.copyProperties(favoriteAddRequest, favorite);

        User user = UserHolder.getUser();
        favorite.setUserId(user.getId());

        // 校验 Favorite 信息是否合法
        favoriteService.validFavorite(favorite, true);

        // 添加收藏夹
        Favorite newFavorite = favoriteService.addFavorite(favorite);

        // 获取收藏夹id
        long newFavoriteId = newFavorite.getId();

        return ResultUtils.success(newFavoriteId);
    }

    @ApiOperation(value = "删除收藏夹")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFavorite(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 Favorite id
        long id = deleteRequest.getId();

        // 判断是否存在
        Favorite oldPost = favoriteService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = favoriteService.deleteFavorite(oldPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    @ApiOperation(value = "获取收藏夹")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Favorite>> listFavoriteVOByPage(@RequestBody FavoriteQueryRequest favoriteQueryRequest) {
        if (favoriteQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = UserHolder.getUser();
        favoriteQueryRequest.setUserId(user.getId());

        long size = favoriteQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(favoriteService.queryFavorite(favoriteQueryRequest));
    }

}
