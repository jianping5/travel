package com.travel.team.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.UserDTO;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.team.model.dto.wall.TeamWallQueryRequest;
import com.travel.team.model.entity.TeamNews;
import com.travel.team.model.entity.TeamWall;
import com.travel.team.model.vo.TeamNewsVO;
import com.travel.team.model.vo.TeamWallVO;
import com.travel.team.service.TeamService;
import com.travel.team.service.TeamWallService;
import com.travel.team.mapper.TeamWallMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【team_wall(团队墙表)】的数据库操作Service实现
* @createDate 2023-03-22 14:40:38
*/
@Service
public class TeamWallServiceImpl extends ServiceImpl<TeamWallMapper, TeamWall>
    implements TeamWallService{

    @DubboReference
    private InnerUserService innerUserService;

    @Override
    public void validTeamWall(TeamWall teamWall, boolean add) {

        if (teamWall == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String content = teamWall.getContent();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评价过长");
        }

    }

    @Override
    public TeamWallVO getTeamWallVO(TeamWall teamWall) {
        TeamWallVO teamWallVO = TeamWallVO.objToVo(teamWall);

        // 1. 关联查询用户信息
        Long userId = teamWall.getUserId();
        UserDTO userDTO = null;
        if (userId != null && userId > 0) {
            userDTO = innerUserService.getUser(userId);
        }
        teamWallVO.setUser(userDTO);

        return teamWallVO;
    }

    @Override
    public Page<TeamWallVO> getTeamWallVOPage(Page<TeamWall> teamWallPage) {
        List<TeamWall> teamWallList = teamWallPage.getRecords();
        Page<TeamWallVO> teamWallVOPage = new Page<>(teamWallPage.getCurrent(), teamWallPage.getSize(), teamWallPage.getTotal());
        if (CollectionUtils.isEmpty(teamWallList)) {
            return teamWallVOPage;
        }
        // 关联查询用户信息
        Set<Long> userIdSet = teamWallList.stream().map(TeamWall::getUserId).collect(Collectors.toSet());
        // 这样做的好处是将 用户 id 和其对应的信息直接对应起来，方便后续获取（而不用遍历获取）
        Map<Long, List<UserDTO>> userIdUserListMap = innerUserService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(UserDTO::getId));

        // 填充信息
        List<TeamWallVO> teamWallVOList = teamWallList.stream().map(teamWall -> {
            TeamWallVO teamWallVO = TeamWallVO.objToVo(teamWall);
            Long userId = teamWall.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            teamWallVO.setUser(userDTO);
            return teamWallVO;
        }).collect(Collectors.toList());
        teamWallVOPage.setRecords(teamWallVOList);
        return teamWallVOPage;
    }

    @Override
    public QueryWrapper<TeamWall> getQueryWrapper(TeamWallQueryRequest teamWallQueryRequest) {
        QueryWrapper<TeamWall> queryWrapper = new QueryWrapper<>();
        if (teamWallQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = teamWallQueryRequest.getSearchText();
        String content = teamWallQueryRequest.getContent();
        String sortField = teamWallQueryRequest.getSortField();
        String sortOrder = teamWallQueryRequest.getSortOrder();
        Long id = teamWallQueryRequest.getId();
        Long userId = teamWallQueryRequest.getUserId();
        Long teamId = teamWallQueryRequest.getTeamId();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("content", searchText);
        }

        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(teamId), "team_id", teamId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public boolean addTeamWall(TeamWall teamWall) {
        boolean result = this.save(teamWall);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean deleteTeamWall(TeamWall teamWall) {
        boolean result = this.removeById(teamWall);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }
}




