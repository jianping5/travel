package com.travel.team.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.team.model.dto.teamApply.TeamApplyQueryRequest;
import com.travel.team.model.dto.teamApply.TeamApplyUpdateRequest;
import com.travel.team.model.entity.TeamApply;
import com.travel.team.model.vo.TeamApplyVO;
import com.travel.team.service.TeamApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 7:24
 */
@Api(tags = "团队申请 Controller")
@RestController
@RequestMapping("/apply")
public class TeamApplyController {

    @Resource
    private TeamApplyService teamApplyService;

    /**
     * 审批加入
     *
     * @param teamApplyUpdateRequest
     * @return
     */
    @ApiOperation("审批加入")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeamApply(@RequestBody TeamApplyUpdateRequest teamApplyUpdateRequest) {
        // 校验团队申请更新请求体
        if (teamApplyUpdateRequest == null || teamApplyUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将团队申请更新请求体的内容赋值给 团队申请
        TeamApply teamApply = new TeamApply();
        BeanUtils.copyProperties(teamApplyUpdateRequest, teamApply);

        // 参数校验
        teamApplyService.validTeamApply(teamApply);
        long id = teamApplyUpdateRequest.getId();

        // 判断是否存在
        TeamApply oldTeamApply = teamApplyService.getById(id);
        ThrowUtils.throwIf(oldTeamApply == null, ErrorCode.NOT_FOUND_ERROR);

        teamApplyService.updateTeamApply(teamApply);

        return ResultUtils.success(true);
    }

    /**
     * 分页获取申请列表（封装类）
     *
     * @param teamApplyQueryRequest
     * @return
     */
    @ApiOperation("分页获取申请列表（封装类）")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<TeamApplyVO>> listTeamApplyVOByPage(@RequestBody TeamApplyQueryRequest teamApplyQueryRequest) {
        if (teamApplyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = teamApplyQueryRequest.getCurrent();
        long size = teamApplyQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<TeamApply> teamApplyPage = teamApplyService.page(new Page<>(current, size),
                teamApplyService.getQueryWrapper(teamApplyQueryRequest));

        return ResultUtils.success(teamApplyService.getTeamApplyVOPage(teamApplyPage));
    }

}
