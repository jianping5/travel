package com.travel.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.user.mapper.FavoriteMapper;
import com.travel.user.model.entity.Favorite;
import com.travel.user.model.request.FavoriteQueryRequest;
import com.travel.user.service.FavoriteService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【favorite(收藏夹)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite>
    implements FavoriteService{
    
        @DubboReference
        private InnerUserService innerUserService;

        @Override
        public void validFavorite(Favorite favorite, boolean add) {
            if (favorite == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }

            Long userId = favorite.getUserId();
            String favoriteName = favorite.getFavoriteName();

            // 创建时，参数不能为空
            if (add) {
                ThrowUtils.throwIf(StringUtils.isEmpty(favoriteName),ErrorCode.PARAMS_ERROR);
                ThrowUtils.throwIf(ObjectUtils.anyNull(userId,favoriteName),ErrorCode.PARAMS_ERROR);
            }
        }

        @Override
        public Page<Favorite> queryFavorite(FavoriteQueryRequest favoriteQueryRequest) {
            QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
            if (favoriteQueryRequest == null) {
                return null;
            }
            // 获取查询条件中的字段
            Long userId = favoriteQueryRequest.getUserId();
            String sortField = favoriteQueryRequest.getSortField();
            String sortOrder = favoriteQueryRequest.getSortOrder();
            long pageSize = favoriteQueryRequest.getPageSize();
            long current = favoriteQueryRequest.getCurrent();

            //拼接查询条件
            queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);

            //排序
            queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                    sortField);


            return page(new Page<>(current, pageSize), queryWrapper);
        }

        @Override
        public Favorite addFavorite(Favorite favorite) {
            User loginUser = UserHolder.getUser();
            Long loginUserId = loginUser.getId();
            // 设置用户 id
            favorite.setUserId(loginUserId);
            boolean save = save(favorite);
            Favorite newFavorite = this.getById(favorite.getId());
            return newFavorite;
        }

        @Override
        public boolean deleteFavorite(Favorite favorite) {
            // 将文章游记状态设置为已下架
            boolean result = removeById(favorite);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return true;
        }

}





    




