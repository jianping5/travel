package com.travel.team.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 7:24
 */
@RestController
@RequestMapping("/team-apply")
public class TeamApplyController {

    @Resource
    private TeamApplyService teamApplyService;

    /**
     * 审批加入
     *
     * @param teamApplyUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeamApply(@RequestBody TeamApplyUpdateRequest teamApplyUpdateRequest) {
        // 校验团队申请更新请求体
        if (teamApplyUpdateRequest == null || teamApplyUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将团队申请更新请求体的内容赋值给 团队
        TeamApply teamApply = new TeamApply();
        BeanUtils.copyProperties(teamApplyUpdateRequest, teamApply);

        // 参数校验
        teamApplyService.validTeamApply(teamApply);
        long id = teamApplyUpdateRequest.getId();

        // 判断是否存在
        TeamApply oldTeamApply = teamApplyService.getById(id);
        ThrowUtils.throwIf(oldTeamApply == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新团队申请状态
        Integer auditState = teamApply.getAuditState();
        UpdateWrapper<TeamApply> teamApplyUpdateWrapper = new UpdateWrapper<>();
        teamApplyUpdateWrapper.set("audit_state", auditState);
        boolean update = teamApplyService.update(teamApplyUpdateWrapper);
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    /**
     * 分页获取申请列表（封装类）
     *
     * @param teamApplyQueryRequest
     * @return
     */
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
