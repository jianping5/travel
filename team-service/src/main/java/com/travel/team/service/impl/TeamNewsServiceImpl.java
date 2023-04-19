package com.travel.team.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.team.mapper.TeamNewsMapper;
import com.travel.team.model.dto.news.TeamNewsQueryRequest;
import com.travel.team.model.entity.Team;
import com.travel.team.model.entity.TeamNews;
import com.travel.team.model.vo.TeamNewsVO;
import com.travel.team.service.TeamNewsService;
import com.travel.team.service.TeamService;
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
* @description 针对表【team_news(团队动态表)】的数据库操作Service实现
* @createDate 2023-03-22 14:40:38
*/
@Service
public class TeamNewsServiceImpl extends ServiceImpl<TeamNewsMapper, TeamNews>
    implements TeamNewsService{

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private TeamService teamService;

    @Resource
    private Gson gson;

    @Override
    public void validTeamNews(TeamNews teamNews, boolean add) {

        if (teamNews == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String content = teamNews.getContent();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "简介过长");
        }

    }

    @Override
    public TeamNewsVO getTeamNewsVO(TeamNews teamNews) {

        TeamNewsVO teamNewsVO = TeamNewsVO.objToVo(teamNews);

        // 1. 关联查询用户信息
        Long userId = teamNews.getUserId();
        UserDTO userDTO = null;
        if (userId != null && userId > 0) {
            userDTO = innerUserService.getUser(userId);
        }
        teamNewsVO.setUser(userDTO);

        return teamNewsVO;
    }

    @Override
    public Page<TeamNewsVO> getTeamNewsVOPage(Page<TeamNews> teamNewsPage) {
        List<TeamNews> teamNewsList = teamNewsPage.getRecords();
        Page<TeamNewsVO> teamNewsVOPage = new Page<>(teamNewsPage.getCurrent(), teamNewsPage.getSize(), teamNewsPage.getTotal());
        if (CollectionUtils.isEmpty(teamNewsList)) {
            return teamNewsVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = teamNewsList.stream().map(TeamNews::getUserId).collect(Collectors.toSet());
        // 这样做的好处是将 用户 id 和其对应的信息直接对应起来，方便后续获取（而不用遍历获取）
        Map<Long, List<UserDTO>> userIdUserListMap = innerUserService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(UserDTO::getId));

        // 填充信息
        List<TeamNewsVO> teamNewsVOList = teamNewsList.stream().map(teamNews -> {
            TeamNewsVO teamNewsVO = TeamNewsVO.objToVo(teamNews);
            Long userId = teamNews.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            teamNewsVO.setUser(userDTO);
            return teamNewsVO;
        }).collect(Collectors.toList());
        teamNewsVOPage.setRecords(teamNewsVOList);
        return teamNewsVOPage;
    }

    @Override
    public QueryWrapper<TeamNews> getQueryWrapper(TeamNewsQueryRequest teamNewsQueryRequest) {
        QueryWrapper<TeamNews> queryWrapper = new QueryWrapper<>();
        if (teamNewsQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = teamNewsQueryRequest.getSearchText();
        String content = teamNewsQueryRequest.getContent();
        String sortField = teamNewsQueryRequest.getSortField();
        String sortOrder = teamNewsQueryRequest.getSortOrder();
        Long id = teamNewsQueryRequest.getId();
        Long userId = teamNewsQueryRequest.getUserId();
        Long teamId = teamNewsQueryRequest.getTeamId();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(teamId), "team_id", teamId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public TeamNews addTeamNews(TeamNews teamNews) {
        // 判断当前用户是否在该团队内
        User loginUser = UserHolder.getUser();
        String teamIdStr = loginUser.getTeamId();
        List<Long> teamIdList = gson.fromJson(teamIdStr, new TypeToken<List<Long>>() {
        }.getType());
        Long teamId = teamNews.getTeamId();
        // 将当前登录用户的 id 设置到 teamNews 里
        teamNews.setUserId(loginUser.getId());

        ThrowUtils.throwIf(!teamIdList.contains(teamId), ErrorCode.NO_AUTH_ERROR);

        // 添加团队动态
        boolean saveResult = this.save(teamNews);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);

        // 更新对应团队的动态数
        UpdateWrapper<Team> teamUpdateWrapper = new UpdateWrapper<>();
        teamUpdateWrapper.eq("id", teamId);
        teamUpdateWrapper.setSql("news_count = news_count + 1");
        boolean updateResult = teamService.update(teamUpdateWrapper);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);

        // 返回添加的团队动态
        return this.getById(teamNews.getId());
    }

    @Override
    public boolean deleteTeamNews(TeamNews teamNews) {
        boolean result = this.removeById(teamNews);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

}




