package com.travel.user.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.travel.common.common.ErrorCode;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.UserDTO;
import com.travel.common.service.InnerUserService;
import com.travel.user.model.entity.User;
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

    @Resource
    private Gson gson;

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
        queryWrapper.select("user_id", "user_name", "user_avatar");
        List<UserInfo> userInfoList = userInfoService.list(queryWrapper);
        List<UserDTO> userDTOList = userInfoList.stream()
                .map(userInfo -> new UserDTO(userInfo.getUserId(), userInfo.getUserName(), userInfo.getUserAvatar()))
                .collect(Collectors.toList());

        return userDTOList;
    }

    @Override
    public boolean addUserTeamId(Long userId, Long teamId) {
        // 根据用户 id 先获取用户，再获取其团队列表字段
        User user = userService.getById(userId);
        String teamIdListStr = user.getTeamId();

        // 将团队列表字段转换为数组
        List<Long> teamIdList = gson.fromJson(teamIdListStr, new TypeToken<List<Long>>() {
        }.getType());

        // 更新数组
        teamIdList.add(teamId);

        // 将数组转换成 Json 字符串并持久化到数据库中
        String teamIdStr = gson.toJson(teamIdList, new TypeToken<List<Long>>() {
        }.getType());

        user.setTeamId(teamIdStr);

        return userService.updateById(user);
    }

    @Override
    public boolean isJoined(Long loginUserId, Long teamId) {
        User user = userService.getById(loginUserId);
        String teamIdStr = user.getTeamId();
        List<Long> teamIdList = gson.fromJson(teamIdStr, new TypeToken<List<Long>>() {}.getType());
        return teamIdList.contains(String.valueOf(teamId));
    }


    @Override
    public boolean changeTeam(Long loginUserId, Long teamId, Integer joinOrQuitOrKick) {
        // 获取用户团队 id 列表
        User user = userService.getById(loginUserId);
        String teamIdStr = user.getTeamId();
        List<Long> teamIdList = gson.fromJson(teamIdStr, new TypeToken<List<Long>>() {}.getType());

        // 加入团队
        if (joinOrQuitOrKick == 0) {
            // 若原先已在团队中，则报错
            ThrowUtils.throwIf(teamIdList.contains(String.valueOf(teamId)), ErrorCode.OPERATION_ERROR);

            // 添加当前团队 id
            teamIdList.add(teamId);
        }

        // 退出团队
        if (joinOrQuitOrKick == 1) {
            // 若原先并未在团队中，则报错
            ThrowUtils.throwIf(!teamIdList.contains(String.valueOf(teamId)), ErrorCode.OPERATION_ERROR);

            // 删除当前团队 id
            teamIdList.remove(teamId);
        }

        // 踢出团队
        if (joinOrQuitOrKick == 2) {
            // 若原先并未在团队中，则报错
            ThrowUtils.throwIf(!teamIdList.contains(String.valueOf(teamId)), ErrorCode.OPERATION_ERROR);

            // 删除当前团队 id
            teamIdList.remove(teamId);
        }

        // 更新用户团队字段
        String teamIdJson = gson.toJson(teamIdList);

        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", loginUserId);
        userUpdateWrapper.set("team_id", teamIdJson);
        boolean updateResult = userService.update(userUpdateWrapper);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);

        return true;
    }

    @Override
    public String getTeamIdStr(Long userId) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id", userId);
        userQueryWrapper.select("team_id");
        User user = userService.getOne(userQueryWrapper);

        String teamId = user.getTeamId();

        return teamId;
    }
}
