package com.travel.user.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travel.common.model.dto.UserDTO;
import com.travel.common.service.InnerUserService;
import com.travel.user.model.entity.UserInfo;
import com.travel.user.service.UserInfoService;
import com.travel.user.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 5:46
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserService userService;

    @Override
    public String sayWorld() {
        return "world";
    }

    @Override
    public UserDTO getUser(Long id) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.select("user_name");
        UserInfo userInfo = userInfoService.getOne(queryWrapper);

        UserDTO userDTO = new UserDTO(id, userInfo.getUserName());
        return userDTO;
    }
}
