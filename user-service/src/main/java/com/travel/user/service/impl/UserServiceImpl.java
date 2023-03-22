package com.travel.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.user.model.entity.User;
import com.travel.user.service.UserService;
import com.travel.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




