package com.travel.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.user.model.entity.Favorite;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.user.model.request.FavoriteQueryRequest;

/**
* @author jianping5
* @description 针对表【favorite(收藏夹)】的数据库操作Service
* @createDate 2023-03-22 14:34:09
*/
public interface FavoriteService extends IService<Favorite> {


    /**
     * 校验 Favorite
     * @param favorite
     * @param b
     */
    void validFavorite(Favorite favorite, boolean b);


    /**
     * 获取收藏夹列表
     */
    Page<Favorite> queryFavorite(FavoriteQueryRequest favoriteQueryRequest);

    /**
     * 创建收藏夹
     * @param Favorite
     * @return
     */
    Favorite addFavorite(Favorite Favorite);


    /**
     * 删除收藏夹
     * @param Favorite
     * @return
     */
    boolean deleteFavorite(Favorite Favorite);
}
