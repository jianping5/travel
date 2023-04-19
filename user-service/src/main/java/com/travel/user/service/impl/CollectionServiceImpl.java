package com.travel.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.user.mapper.CollectionMapper;
import com.travel.user.model.dto.CollectionVO;
import com.travel.user.model.entity.Collection;
import com.travel.user.model.entity.Favorite;
import com.travel.user.model.request.CollectionQueryRequest;
import com.travel.user.model.request.CollectionRequest;
import com.travel.user.service.CollectionService;
import com.travel.user.service.FavoriteService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【collection(收藏表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection>
    implements CollectionService{
    @Resource
    private FavoriteService favoriteService;


    @Override
    public void validCollection(CollectionRequest collectionRequest, boolean add) {
        if (collectionRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = collectionRequest.getUserId();
        Integer collectionObjType = collectionRequest.getCollectionObjType();
        Long collectionObjId = collectionRequest.getCollectionObjId();
        Integer requestType = collectionRequest.getRequestType();
        Long favoriteId = collectionRequest.getFavoriteId();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(ObjectUtils.anyNull(userId,collectionObjType,collectionObjId,requestType),ErrorCode.PARAMS_ERROR);
        }
        if(requestType.equals(1)){
            ThrowUtils.throwIf(ObjectUtils.anyNull(favoriteId)||favoriteId<=0,ErrorCode.PARAMS_ERROR);
        }
    }

    @Override
    public Page<CollectionVO> getCollectionVOPage(Page<Collection> collectionPage) {
        if(collectionPage == null){
            return null;
        }
        List<Collection> collectionList = collectionPage.getRecords();
        Page<CollectionVO> collectionVOPage = new Page<>(collectionPage.getCurrent(), collectionPage.getSize(), collectionPage.getTotal());
        if (CollectionUtils.isEmpty(collectionList)) {
            return collectionVOPage;
        }
        //填充信息
        List<CollectionVO> collectionVOList = getCollectionVOList(collectionList);
        collectionVOPage.setRecords(collectionVOList);
        return collectionVOPage;
    }

    /**
     * 根据文章列表获取文章视图体列表
     * @param collectionList
     * @return
     */
    public List<CollectionVO> getCollectionVOList(List<Collection> collectionList) {
        Set<Long> collect = collectionList.stream().map(k -> k.getFavoriteId()).collect(Collectors.toSet());
        HashMap<Long, String> map = new HashMap<>();
        List<Favorite> favorites = favoriteService.list(new QueryWrapper<Favorite>().in("id", collect));
        favorites.forEach(t->{
            map.put(t.getId(),t.getFavoriteName());
        });
        // 填充信息
        List<CollectionVO> collectionVOList = collectionList.stream().map(collection -> {
            // 获取官方视图体
            CollectionVO collectionVO = CollectionVO.objToVo(collection);
            String name = map.get(collection.getFavoriteId());
            if(StringUtils.isEmpty(name)){
                collectionVO.setFavoriteName("默认收藏夹");
            }
            collectionVO.setFavoriteName(name);
            return collectionVO;
        }).collect(Collectors.toList());
        return collectionVOList;
    }

    @Override
    public Page<Collection> queryCollection(CollectionQueryRequest collectionQueryRequest) {
        QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
        if (collectionQueryRequest == null) {
            return null;
        }
        // 获取查询条件中的字段
        Long userId = collectionQueryRequest.getUserId();
        String sortField = collectionQueryRequest.getSortField();
        String sortOrder = collectionQueryRequest.getSortOrder();
        long pageSize = collectionQueryRequest.getPageSize();
        long current = collectionQueryRequest.getCurrent();

        //拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);

        //排序
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return page(new Page<>(current, pageSize), queryWrapper);
    }

    @Override
    public void executeCollection(Collection collection,Boolean isAdd) {
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        // 设置用户 id
        collection.setUserId(loginUserId);
        QueryWrapper<Collection> eq = new QueryWrapper<Collection>()
                .eq("user_id", loginUserId)
                .eq("favorite_id",collection.getFavoriteId())
                .eq("collection_obj_type", collection.getCollectionObjType())
                .eq("collection_obj_id", collection.getCollectionObjId());
        Collection oldCollection = getOne(eq);
        if(oldCollection==null){
            if(!isAdd){ return; }
            boolean save = save(collection);
            ThrowUtils.throwIf(!save,ErrorCode.SYSTEM_ERROR);
        }else {
            if(isAdd){  return; }
            boolean remove = removeById(oldCollection);
            ThrowUtils.throwIf(!remove,ErrorCode.SYSTEM_ERROR);
        }
    }

}




