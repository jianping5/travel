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
import com.travel.team.model.dto.news.TeamNewsAddRequest;
import com.travel.team.model.dto.news.TeamNewsQueryRequest;
import com.travel.team.model.entity.TeamNews;
import com.travel.team.model.vo.TeamNewsVO;
import com.travel.team.service.TeamNewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 7:24
 */
@Api(tags = "团队动态 Controller")
@RestController
@RequestMapping("/news")
public class TeamNewsController {

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private TeamNewsService teamNewsService;

    // region 增删改查

    /**
     * 发布团队动态
     *
     * @param teamNewsAddRequest
     * @return
     */
    @ApiOperation(value = "发布团队动态")
    @PostMapping("/add")
    public BaseResponse<TeamNews> addTeamNews(@RequestBody TeamNewsAddRequest teamNewsAddRequest) {
        // 校验请求体
        if (teamNewsAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将 请求体的内容赋值到 teamNews 中
        TeamNews teamNews = new TeamNews();
        BeanUtils.copyProperties(teamNewsAddRequest, teamNews);

        // 校验 teamNews 信息是否合法
        teamNewsService.validTeamNews(teamNews, true);

        // 添加到团队中，并返回
        return ResultUtils.success(teamNewsService.addTeamNews(teamNews));
    }

    /**
     * 删除团队动态
     *
     * @param deleteRequest
     * @return
     */
    @ApiOperation(value = "删除团队动态")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeamNews(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 team id
        long id = deleteRequest.getId();

        // 判断是否存在
        TeamNews oldTeamNews = teamNewsService.getById(id);
        ThrowUtils.throwIf(oldTeamNews == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!oldTeamNews.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(2)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        teamNewsService.deleteTeamNews(oldTeamNews);

        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取团队动态
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据 id 获取团队动态")
    @GetMapping("/get/vo")
    public BaseResponse<TeamNewsVO> getTeamNewsVOById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        TeamNews teamNews = teamNewsService.getById(id);
        if (teamNews == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(teamNewsService.getTeamNewsVO(teamNews));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param teamNewsQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取列表（封装类）")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<TeamNewsVO>> listTeamNewsVOByPage(@RequestBody TeamNewsQueryRequest teamNewsQueryRequest) {
        long current = teamNewsQueryRequest.getCurrent();
        long size = teamNewsQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<TeamNews> teamNewsPage = teamNewsService.page(new Page<>(current, size),
                teamNewsService.getQueryWrapper(teamNewsQueryRequest));
        return ResultUtils.success(teamNewsService.getTeamNewsVOPage(teamNewsPage));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param teamNewsQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取当前用户创建的资源列表")
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<TeamNewsVO>> listMyTeamNewsVOByPage(@RequestBody TeamNewsQueryRequest teamNewsQueryRequest) {
        //
        if (teamNewsQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = UserHolder.getUser();
        teamNewsQueryRequest.setUserId(loginUser.getId());
        long current = teamNewsQueryRequest.getCurrent();
        long size = teamNewsQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<TeamNews> teamNewsPage = teamNewsService.page(new Page<>(current, size),
                teamNewsService.getQueryWrapper(teamNewsQueryRequest));

        return ResultUtils.success(teamNewsService.getTeamNewsVOPage(teamNewsPage));
    }

    // endregion
}
