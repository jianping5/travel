package com.travel.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.user.model.entity.UserInfo;
import com.travel.user.service.UserInfoService;
import com.travel.user.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【user_info(用户基本信息表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

}




