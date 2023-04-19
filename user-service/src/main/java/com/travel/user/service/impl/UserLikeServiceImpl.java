package com.travel.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.user.model.entity.UserLike;
import com.travel.user.model.request.UserLikeRequest;
import com.travel.user.registry.ServiceRegistry;
import com.travel.user.registry.UpdateWrapperRegistry;
import com.travel.user.service.UserLikeService;
import com.travel.user.mapper.UserLikeMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author jianping5
* @description 针对表【user_like(点赞表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike>
    implements UserLikeService{
    @Resource
    private ServiceRegistry serviceRegistry;
    @Resource
    private UpdateWrapperRegistry updateWrapperRegistry;
    @Override
    public void validUserLike(UserLikeRequest userLikeRequest, boolean add) {
        if (userLikeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = userLikeRequest.getUserId();
        Integer likeState = userLikeRequest.getLikeState();
        Integer likeObjType = userLikeRequest.getLikeObjType();
        Long likeObjId = userLikeRequest.getLikeObjId();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(ObjectUtils.anyNull(userId,likeState,likeObjType,likeObjId),ErrorCode.PARAMS_ERROR);
        }
    }

    @Override
    public void executeUserLike(UserLike userLike) {
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        // 设置用户 id
        userLike.setUserId(loginUserId);

        // 更新数据库
        QueryWrapper<UserLike> eq = new QueryWrapper<UserLike>()
                .eq("user_id", loginUserId)
                .eq("like_obj_type",userLike.getLikeObjType())
                .eq("like_obj_id", userLike.getLikeObjId());
        UserLike oldUserLike = getOne(eq);
        if(oldUserLike==null){
            boolean save = save(userLike);
            ThrowUtils.throwIf(!save,ErrorCode.SYSTEM_ERROR);
        }else {
            oldUserLike.setLikeState(userLike.getLikeState());
            boolean update = updateById(userLike);
            ThrowUtils.throwIf(!update,ErrorCode.SYSTEM_ERROR);
        }
        //更新对应实体的点赞数
        IService<?> service = serviceRegistry.getServiceByType(userLike.getLikeObjType());
        UpdateWrapper<?> updateWrapper = updateWrapperRegistry.getUpdateWrapperByType(userLike.getLikeObjType());
        updateWrapper.eq("id", userLike.getLikeObjId());
        if(userLike.getLikeState().equals(0)){
            updateWrapper.setSql("like_count = like_count + 1");
        }else {
            updateWrapper.setSql("like_count = like_count - 1");
        }
        // 执行
        service.update((Wrapper) updateWrapper);
    }

}




