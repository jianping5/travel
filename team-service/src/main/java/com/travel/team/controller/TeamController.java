package com.travel.team.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
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
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.client.ml.EvaluateDataFrameRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.awt.geom.QuadCurve2D;
import java.util.List;

/**
 * @author jianping5
 * @createDate 18/3/2023 下午 9:48
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private TeamService teamService;

    // region 增删改查

    /**
     * 创建团队
     *
     * @param teamAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Team> addTeam(@RequestBody TeamAddRequest teamAddRequest) {
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

        return ResultUtils.success(result);
    }

    /**
     * 更新团队
     *
     * @param teamUpdateRequest
     * @return
     */
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
        Team oldPost = teamService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

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
     * 分页获取列表（封装类）
     *
     * @param teamQueryRequest
     * @return
     */
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
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<TeamVO>> listMyTeamVOByPage(@RequestBody TeamQueryRequest teamQueryRequest) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = UserHolder.getUser();
        teamQueryRequest.setUserId(loginUser.getId());
        long current = teamQueryRequest.getCurrent();
        long size = teamQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Team> postPage = teamService.page(new Page<>(current, size),
                teamService.getQueryWrapper(teamQueryRequest));

        return ResultUtils.success(teamService.getTeamVOPage(postPage));
    }

    /**
     * 获取指定用户加入的团队列表
     *
     * @param teamQueryRequest
     * @return
     */
    @PostMapping("/my/join")
    public BaseResponse<List<Team>> listMyTeam(@RequestBody TeamQueryRequest teamQueryRequest) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = teamQueryRequest.getUserId();

        return ResultUtils.success(teamService.listMyTeam(userId));
    }

    // endregion

    /**
     * 更换团队（加入团队、退出团队、踢出成员）
     *
     * @param teamChangeRequest
     * @return
     */
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
        }

        // 当前登录用户进行更换团队
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        teamService.changeTeam(loginUserId, teamId, joinOrQuitOrKick);

        return ResultUtils.success(true);
    }


}
