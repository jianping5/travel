package com.travel.team.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.UserHolder;
import com.travel.team.model.dto.team.TeamAddRequest;
import com.travel.team.model.dto.team.TeamChangeRequest;
import com.travel.team.model.dto.team.TeamQueryRequest;
import com.travel.team.model.dto.team.TeamUpdateRequest;
import com.travel.team.model.entity.Team;
import com.travel.team.model.vo.TeamVO;
import com.travel.team.service.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jianping5
 * @createDate 18/3/2023 下午 9:48
 */
@RestController
@Api(tags = "团队 Controller")
public class TeamController {

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private TeamService teamService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private Gson gson;

    /**
     * 创建团队
     *
     * @param teamAddRequest
     * @return
     */
    @ApiOperation(value = "创建团队")
    @PostMapping("/add")
    public BaseResponse<Team> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        // 校验请求体
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 team 中
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);

        // 校验 team 信息是否合法
        teamService.validTeam(team, true);

        // 添加到团队中
        Team newTeam = teamService.addTeam(team);

        // 获取团队 id
        long newTeamId = newTeam.getId();

        // 并更新缓存
        String token = request.getHeader("token");
        RMap<String, Object> map = redissonClient.getMap("user_login:" + token);
        String teamIdListJson = (String) map.get("teamId");
        System.out.println(teamIdListJson);
        List<Long> teamIdList = gson.fromJson(teamIdListJson, new TypeToken<List<Long>>() {
        }.getType());
        teamIdList.add(newTeamId);
        map.put("teamId", gson.toJson(teamIdList));

        // 更新用户表中的团队字段
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        innerUserService.addUserTeamId(loginUserId, newTeamId);

        return ResultUtils.success(newTeam);
    }

    /**
     * 解散团队
     *
     * @param deleteRequest
     * @return
     */
    @ApiOperation(value = "解散团队")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 team id
        long id = deleteRequest.getId();

        // 判断是否存在
        Team oldPost = teamService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(2)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = teamService.deleteTeam(oldPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    /**
     * 更新团队
     *
     * @param teamUpdateRequest
     * @return
     */
    @ApiOperation(value = "更新团队")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest) {
        // 校验团队更新请求体
        if (teamUpdateRequest == null || teamUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将团队更新请求体的内容赋值给 团队
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, team);

        // 参数校验
        teamService.validTeam(team, false);
        long id = teamUpdateRequest.getId();

        // 判断是否存在
        Team oldTeam = teamService.getById(id);
        ThrowUtils.throwIf(oldTeam == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新团队
        teamService.updateTeam(team);

        return ResultUtils.success(true);
    }


    /**
     * 根据 id 获取团队介绍详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据 id 获取团队介绍详情")
    @GetMapping("/get/vo")
    public BaseResponse<TeamVO> getTeamVOById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(teamService.getTeamVO(team));
    }


    /**
     * 分页获取团队列表（封装类）
     *
     * @param teamQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取团队列表")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<TeamVO>> listTeamVOByPage(@RequestBody TeamQueryRequest teamQueryRequest) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = teamQueryRequest.getCurrent();
        long size = teamQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Team> teamPage = teamService.page(new Page<>(current, size),
                teamService.getQueryWrapper(teamQueryRequest));

        return ResultUtils.success(teamService.getTeamVOPage(teamPage));
    }

    /**
     * 获取推荐团队（大众化推荐）
     *
     * @param teamQueryRequest
     * @return
     */
    @ApiOperation(value = "获取推荐团队（大众化推荐）")
    @PostMapping("/rcmd")
    public BaseResponse<List<TeamVO>> listRcmdTeamVO(@RequestBody TeamQueryRequest teamQueryRequest) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = teamQueryRequest.getCurrent();
        long size = teamQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        List<TeamVO> teamVOList = teamService.listRcmdTeamVO(current, size);

        return ResultUtils.success(teamVOList);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param teamQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取当前用户创建的资源列表")
    @PostMapping("/my/list/page/vo")
    public BaseResponse<List<Team>> listMyTeamVOByPage(@RequestBody TeamQueryRequest teamQueryRequest) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = UserHolder.getUser();

        return ResultUtils.success(teamService.listMyCreateTeam(loginUser.getId()));
    }

    /**
     * 获取指定用户加入的团队列表
     *
     * @param teamQueryRequest
     * @return
     */
    @ApiOperation(value = "获取指定用户加入的团队列表")
    @PostMapping("/join/list")
    public BaseResponse<List<Team>> listMyTeam(@RequestBody TeamQueryRequest teamQueryRequest) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = teamQueryRequest.getUserId();

        return ResultUtils.success(teamService.listMyTeam(userId));
    }

    /**
     * 更换团队（加入团队、退出团队、踢出成员）
     *
     * @param teamChangeRequest
     * @return
     */
    @ApiOperation(value = "更换团队（加入团队、退出团队、踢出成员）")
    @PostMapping("/change")
    public BaseResponse<Boolean> changeTeam(@RequestBody TeamChangeRequest teamChangeRequest) {
        // 校验请求体
        if (teamChangeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamChangeRequest.getTeamId();
        Integer joinOrQuitOrKick = teamChangeRequest.getJoinOrQuitOrKick();

        if (teamId==null || teamId<0 || joinOrQuitOrKick==null || joinOrQuitOrKick<0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        // 选择指定用户进行踢出
        Long userId = teamChangeRequest.getUserId();
        if (userId != null) {
            teamService.changeTeam(userId, teamId, joinOrQuitOrKick);
            return ResultUtils.success(true);
        }

        // 当前登录用户进行更换团队
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        teamService.changeTeam(loginUserId, teamId, joinOrQuitOrKick);

        return ResultUtils.success(true);
    }

    /**
     * 查询团队所有成员
     * @param teamId
     * @return
     */
    @ApiOperation(value = "查询团队所有成员")
    @GetMapping("/member/{teamId}")
    public BaseResponse<List<UserDTO>> listTeamMember(@PathVariable Long teamId) {
        // 校验参数
        if (teamId == null || teamId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 返回结果
        return ResultUtils.success(teamService.listTeamMember(teamId));
    }

}
