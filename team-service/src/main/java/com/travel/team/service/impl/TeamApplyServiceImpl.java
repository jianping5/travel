package com.travel.team.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.team.mapper.TeamApplyMapper;
import com.travel.team.mapper.TeamMapper;
import com.travel.team.model.dto.teamApply.TeamApplyQueryRequest;
import com.travel.team.model.entity.Team;
import com.travel.team.model.entity.TeamApply;
import com.travel.team.model.vo.TeamApplyVO;
import com.travel.team.service.TeamApplyService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【team_apply(团队申请表)】的数据库操作Service实现
* @createDate 2023-03-22 14:40:38
*/
@Service
public class TeamApplyServiceImpl extends ServiceImpl<TeamApplyMapper, TeamApply>
    implements TeamApplyService{

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private InnerUserService innerUserService;

    @Override
    public void validTeamApply(TeamApply teamApply) {
        if (teamApply == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验申请状态
        Integer auditState = teamApply.getAuditState();
        List<Integer> list = Arrays.asList(0, 1, 2);

        ThrowUtils.throwIf(!list.contains(auditState), ErrorCode.PARAMS_ERROR);
    }

    @Override
    public boolean updateTeamApply(TeamApply teamApply) {
        // 判断当前用户是否为当前团队的创始人
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long teamId = teamApply.getTeamId();

        // 获取对应团队的创始人 id
        Team team = teamMapper.selectById(teamId);
        Long userId = team.getUserId();

        ThrowUtils.throwIf(!loginUserId.equals(userId), ErrorCode.NO_AUTH_ERROR);

        // 更新数据库
        boolean updateResult = this.updateById(teamApply);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);

        return true;
    }

    @Override
    public QueryWrapper<TeamApply> getQueryWrapper(TeamApplyQueryRequest teamApplyQueryRequest) {
        QueryWrapper<TeamApply> queryWrapper = new QueryWrapper<>();
        if (teamApplyQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询条件中的字段
        String searchText = teamApplyQueryRequest.getSearchText();
        String sortField = teamApplyQueryRequest.getSortField();
        String sortOrder = teamApplyQueryRequest.getSortOrder();
        Long id = teamApplyQueryRequest.getId();
        Long userId = teamApplyQueryRequest.getUserId();
        Integer teamState = teamApplyQueryRequest.getTeamState();
        Integer auditState = teamApplyQueryRequest.getAuditState();

        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        queryWrapper.eq(ObjectUtils.isNotEmpty(teamState), "team_state", 0);
        queryWrapper.eq(ObjectUtils.isNotEmpty(auditState), "audit_state", 0);
        return queryWrapper;
    }

    @Override
    public Page<TeamApplyVO> getTeamApplyVOPage(Page<TeamApply> teamApplyPage) {
        List<TeamApply> teamApplyList = teamApplyPage.getRecords();
        Page<TeamApplyVO> teamApplyVOPage = new Page<>(teamApplyPage.getCurrent(), teamApplyPage.getSize(), teamApplyPage.getTotal());
        if (CollectionUtils.isEmpty(teamApplyList)) {
            return teamApplyVOPage;
        }

        // 根据团队申请列表获取团队申请视图体列表
        List<TeamApplyVO> teamApplyVOList = getTeamVOList(teamApplyList);

        teamApplyVOPage.setRecords(teamApplyVOList);

        return teamApplyVOPage;
    }

    private List<TeamApplyVO> getTeamVOList(List<TeamApply> teamApplyList) {
        // 1. 关联查询用户信息（这样做的好处是将 用户 id 和其对应的信息直接对应起来，方便后续获取（而不用遍历获取)）
        Set<Long> userIdSet = teamApplyList.stream().map(TeamApply::getUserId).collect(Collectors.toSet());

        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);
        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));

        // 填充信息
        List<TeamApplyVO> teamApplyVOList = teamApplyList.stream().map(teamApply -> {
            TeamApplyVO teamApplyVO = TeamApplyVO.objToVo(teamApply);
            Long userId = teamApply.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            teamApplyVO.setUser(userDTO);
            return teamApplyVO;
        }).collect(Collectors.toList());

        return teamApplyVOList;
    }
}




