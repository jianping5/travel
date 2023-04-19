package com.travel.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.user.model.dto.CollectionVO;
import com.travel.user.model.dto.FollowVO;
import com.travel.user.model.entity.Collection;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.user.model.entity.Follow;
import com.travel.user.model.request.CollectionQueryRequest;
import com.travel.user.model.request.CollectionRequest;
import com.travel.user.model.request.FollowQueryRequest;
import com.travel.user.model.request.FollowRequest;

/**
* @author jianping5
* @description 针对表【collection(收藏表)】的数据库操作Service
* @createDate 2023-03-22 14:34:09
*/
public interface CollectionService extends IService<Collection> {

    void executeCollection(Collection collection,Boolean isAdd);
    /**
     * 校验 Collection
     * @param collectionRequest
     * @param b
     */
    void validCollection(CollectionRequest collectionRequest, boolean b);
    /**
     * 获取收藏列表Page
     */
    Page<CollectionVO> getCollectionVOPage(Page<Collection> CollectionPage);
    /**
     * 获取收藏列表
     */
    Page<Collection> queryCollection(CollectionQueryRequest collectionQueryRequest);

}
