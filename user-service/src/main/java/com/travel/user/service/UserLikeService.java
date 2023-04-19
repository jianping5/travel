package com.travel.user.service;

import com.travel.user.model.entity.Collection;
import com.travel.user.model.entity.UserLike;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.user.model.request.CollectionRequest;
import com.travel.user.model.request.UserLikeRequest;

/**
* @author jianping5
* @description 针对表【user_like(点赞表)】的数据库操作Service
* @createDate 2023-03-22 14:34:09
*/
public interface UserLikeService extends IService<UserLike> {
    /**
     * 点赞用户、文章游记、视频游记、评论
     */
    void executeUserLike(UserLike userLike);
    /**
     * 校验 UserLike
     * @param userLikeRequest
     * @param b
     */
    void validUserLike(UserLikeRequest userLikeRequest, boolean b);

}
