package com.travel.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.user.model.entity.UserLike;
import com.travel.user.service.UserLikeService;
import com.travel.user.mapper.UserLikeMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【user_like(点赞表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike>
    implements UserLikeService{

}




