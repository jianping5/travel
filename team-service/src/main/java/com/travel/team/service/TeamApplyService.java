package com.travel.team.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.team.model.dto.teamApply.TeamApplyQueryRequest;
import com.travel.team.model.entity.TeamApply;
import com.travel.team.model.vo.TeamApplyVO;

/**
* @author jianping5
* @description 针对表【team_apply(团队申请表)】的数据库操作Service
* @createDate 2023-03-22 14:40:38
*/
public interface TeamApplyService extends IService<TeamApply> {

    /**
     * 校验团队申请
     * @param teamApply
     */
    void validTeamApply(TeamApply teamApply);

    /**
     * 更新团队申请
     * @param teamApply
     * @return
     */
    boolean updateTeamApply(TeamApply teamApply);

    /**
     * 根据请求体获取请求 Wrapper
     * @param teamApplyQueryRequest
     * @return
     */
    QueryWrapper<TeamApply> getQueryWrapper(TeamApplyQueryRequest teamApplyQueryRequest);

    /**
     * 获取分页团队申请视图体
     * @param teamApplyPage
     * @return
     */
    Page<TeamApplyVO> getTeamApplyVOPage(Page<TeamApply> teamApplyPage);
}
