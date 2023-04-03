package com.travel.team.controller;

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
import com.travel.team.model.dto.team.TeamQueryRequest;
import com.travel.team.model.dto.team.TeamUpdateRequest;
import com.travel.team.model.dto.wall.TeamWallAddRequest;
import com.travel.team.model.dto.wall.TeamWallQueryRequest;
import com.travel.team.model.entity.Team;
import com.travel.team.model.entity.TeamWall;
import com.travel.team.model.vo.TeamVO;
import com.travel.team.model.vo.TeamWallVO;
import com.travel.team.service.TeamService;
import com.travel.team.service.TeamWallService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 8:35
 */
@RestController
@RequestMapping("/team-wall")
public class TeamWallController {
    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private TeamWallService teamWallService;

    // region 增删改查

    /**
     * 创建
     *
     * @param teamWallAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<TeamWall> addTeamWall(@RequestBody TeamWallAddRequest teamWallAddRequest) {
        // 校验请求体
        if (teamWallAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 team 中
        TeamWall teamWall = new TeamWall();
        BeanUtils.copyProperties(teamWallAddRequest, teamWall);

        // 校验 team 信息是否合法
        teamWallService.validTeamWall(teamWall, true);

        // 添加到团队中
        teamWallService.addTeamWall(teamWall);

        return ResultUtils.success(teamWall);
    }

    /**
     * 删除
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
        TeamWall teamWall = teamWallService.getById(id);
        ThrowUtils.throwIf(teamWall == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!teamWall.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(2)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        teamWallService.deleteTeamWall(teamWall);

        return ResultUtils.success(true);
    }


    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<TeamWallVO> getTeamVOById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        TeamWall teamWall = teamWallService.getById(id);
        if (teamWall == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(teamWallService.getTeamWallVO(teamWall));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param teamWallQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<TeamWallVO>> listTeamVOByPage(@RequestBody TeamWallQueryRequest teamWallQueryRequest) {
        long current = teamWallQueryRequest.getCurrent();
        long size = teamWallQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<TeamWall> teamWallPage = teamWallService.page(new Page<>(current, size),
                teamWallService.getQueryWrapper(teamWallQueryRequest));
        return ResultUtils.success(teamWallService.getTeamWallVOPage(teamWallPage));
    }
}
