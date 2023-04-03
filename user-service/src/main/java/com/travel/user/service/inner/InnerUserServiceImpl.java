package com.travel.user.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travel.common.model.dto.UserDTO;
import com.travel.common.service.InnerUserService;
import com.travel.user.model.entity.UserInfo;
import com.travel.user.service.UserInfoService;
import com.travel.user.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public UserDTO getUser(Long id) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        queryWrapper.select("user_id");
        queryWrapper.select("user_name");
        queryWrapper.select("user_avatar");
        UserInfo userInfo = userInfoService.getOne(queryWrapper);

        UserDTO userDTO = new UserDTO(id, userInfo.getUserName(), userInfo.getUserAvatar());
        return userDTO;
    }

    @Override
    public List<UserDTO> listByIds(Set<Long> userIdList) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIdList);
        queryWrapper.select("user_name");
        queryWrapper.select("user_avatar");
        List<UserDTO> userDTOList = userInfoService.list(queryWrapper).stream()
                .map(userInfo -> new UserDTO(userInfo.getUserId(), userInfo.getUserName(), userInfo.getUserAvatar()))
                .collect(Collectors.toList());

        return userDTOList;
    }

    @Override
    public boolean addUserTeamId(Long userId, Long teamId) {
        return false;
    }

    @Override
    public boolean isJoined(Long loginUserId, Long teamId) {
        return false;
    }

    @Override
    public boolean changeTeam(Long loginUserId, Long teamId, Integer joinOrQuitOrKick) {
        return false;
    }

    @Override
    public String getTeamIdStr(Long userId) {
        return null;
    }
}
