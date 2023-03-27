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
import com.travel.team.model.dto.team.TeamQueryRequest;
import com.travel.team.model.entity.Team;
import com.travel.team.model.vo.TeamVO;
import com.travel.team.service.TeamService;
import com.travel.team.mapper.TeamMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【team(团队表)】的数据库操作Service实现
* @createDate 2023-03-22 14:40:38
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void validTeam(Team team, boolean add) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String teamName = team.getTeamName();
        String intro = team.getIntro();
        // todo: 这两者可以考虑使用默认值（若用户不传）
        String coverUrl = team.getCoverUrl();
        String iconUrl = team.getIconUrl();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(teamName, intro), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(teamName) && teamName.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "团队名称过长");
        }
        if (StringUtils.isNotBlank(intro) && intro.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    @Override
    public TeamVO getTeamVO(Team team) {
        TeamVO teamVO = TeamVO.objToVo(team);
        long teamId = team.getId();

        // 1. 关联查询用户信息
        Long userId = team.getUserId();
        UserDTO userDTO = null;
        if (userId != null && userId > 0) {
            userDTO = innerUserService.getUser(userId);
        }
        teamVO.setUser(userDTO);

        // 2. 已登录，获取用户点赞、收藏状态
        User loginUser = UserHolder.getUser();
        if (loginUser != null) {
            // todo: 获取点赞（从 redis 中获取）并设置到 teamVO 中


            // todo: 获取收藏（从 redis 中获取）并设置到 teamVO 中
        }
        return teamVO;
    }

    @Override
    public Page<TeamVO> getTeamVOPage(Page<Team> teamPage) {
        List<Team> teamList = teamPage.getRecords();
        Page<TeamVO> teamVOPage = new Page<>(teamPage.getCurrent(), teamPage.getSize(), teamPage.getTotal());
        if (CollectionUtils.isEmpty(teamList)) {
            return teamVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = teamList.stream().map(Team::getUserId).collect(Collectors.toSet());
        // 这样做的好处是将 用户 id 和其对应的信息直接对应起来，方便后续获取（而不用遍历获取）
        Map<Long, List<UserDTO>> userIdUserListMap = innerUserService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(UserDTO::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> postIdHasLikeMap = new HashMap<>();
        Map<Long, Boolean> postIdHasFavourMap = new HashMap<>();
        User loginUser = UserHolder.getUser();
        if (loginUser != null) {
            Set<Long> teamIdSet = teamList.stream().map(Team::getId).collect(Collectors.toSet());

            // todo: 获取点赞（从 redis 中获取）并设置到 teamVO 中


            // todo: 获取收藏（从 redis 中获取）并设置到 teamVO 中

        }
        // 填充信息
        List<TeamVO> postVOList = teamList.stream().map(post -> {
            TeamVO teamVO = TeamVO.objToVo(post);
            Long userId = post.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            teamVO.setUser(userDTO);
            teamVO.setHasLike(teamIdHasLikeMap.getOrDefault(post.getId(), false));
            teamVO.setHasFavour(teamIdHasFavourMap.getOrDefault(post.getId(), false));
            return teamVO;
        }).collect(Collectors.toList());
        teamVOPage.setRecords(postVOList);
        return teamVOPage;
    }

    /**
     * 获取查询包装类
     *
     * @param teamQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Team> getQueryWrapper(TeamQueryRequest teamQueryRequest) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = teamQueryRequest.getSearchText();
        String sortField = teamQueryRequest.getSortField();
        String sortOrder = teamQueryRequest.getSortOrder();
        Long id = teamQueryRequest.getId();
        String name = teamQueryRequest.getName();
        String intro = teamQueryRequest.getIntro();
        Long userId = teamQueryRequest.getUserId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("name", name).or().like("intro", intro);
        }
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(intro), "intro", intro);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




