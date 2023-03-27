package com.travel.team.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.constant.UserConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.UserHolder;
import com.travel.team.model.dto.team.TeamAddRequest;
import com.travel.team.model.dto.team.TeamQueryRequest;
import com.travel.team.model.dto.team.TeamUpdateRequest;
import com.travel.team.model.entity.Team;
import com.travel.team.model.vo.TeamVO;
import com.travel.team.service.TeamService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.temporal.Temporal;
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
     * 创建
     *
     * @param teamAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody TeamAddRequest teamAddRequest) {
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
        boolean result = teamService.save(team);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回团队 id
        long newTeamId = team.getId();
        return ResultUtils.success(newTeamId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
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
        boolean result = teamService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(result);
    }

    /**
     * 更新（仅管理员）
     *
     * @param teamUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePost(@RequestBody TeamUpdateRequest teamUpdateRequest) {
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
        boolean result = teamService.updateById(team);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<TeamVO> getPostVOById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        Team post = teamService.getById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(teamService.getTeamVO(post));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param teamQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<TeamVO>> listPostVOByPage(@RequestBody TeamQueryRequest teamQueryRequest,
                                                       HttpServletRequest request) {
        long current = teamQueryRequest.getCurrent();
        long size = teamQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Team> postPage = teamService.page(new Page<>(current, size),
                teamService.getQueryWrapper(teamQueryRequest));
        return ResultUtils.success(teamService.getTeamVOPage(postPage));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param teamQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<TeamVO>> listMyPostVOByPage(@RequestBody TeamQueryRequest teamQueryRequest,
                                                         HttpServletRequest request) {
        //
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

    // endregion

}
