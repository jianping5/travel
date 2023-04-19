package com.travel.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.user.model.entity.UserInfo;

import java.util.Date;
import java.util.List;

/**
* @author jianping5
* @description 针对表【user_info(用户基本信息表)】的数据库操作Mapper
* @createDate 2023-03-22 14:34:09
* @Entity com.travel.common.model.entity.UserInfo
*/
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 查询某段时间的用户列表（包括已被删除的数据）
     * @param minUpdateTime
     * @return
     */
    List<UserInfo> listUserWithDelete(Date minUpdateTime);
}




