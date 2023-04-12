package com.travel.team.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.model.dto.UserDTO;
import com.travel.team.model.dto.team.TeamQueryRequest;
import com.travel.team.model.entity.Team;
import com.travel.team.model.vo.TeamVO;

import java.util.List;

/**
* @author jianping5
* @description 针对表【team(团队表)】的数据库操作Service
* @createDate 2023-03-22 14:40:38
*/
public interface TeamService extends IService<Team> {

    /**
     * 校验 Team
     * @param team
     * @param b
     */
    void validTeam(Team team, boolean b);

    /**
     * 获取团队视图体
     * @param team
     * @return
     */
    TeamVO getTeamVO(Team team);

    /**
     * 获取分页团队视图体
     * @param teamPage
     * @return
     */
    Page<TeamVO> getTeamVOPage(Page<Team> teamPage);

    /**
     * 根据请求体获取请求 Wrapper
     * @param teamQueryRequest
     * @return
     */
    QueryWrapper<Team> getQueryWrapper(TeamQueryRequest teamQueryRequest);

    /**
     * 创建团队
     * @param team
     * @return
     */
    Team addTeam(Team team);


    /**
     * 解散团队
     * @param team
     * @return
     */
    boolean deleteTeam(Team team);

    /**
     * 更新团队
     * @param team
     * @return
     */
    boolean updateTeam(Team team);

    /**
     * 加入团队
     * @param userId
     * @param teamId
     * @param joinOrQuitOrKick
     * @return
     */
    boolean changeTeam(Long userId, Long teamId, Integer joinOrQuitOrKick);

    /**
     * 根据用户 id 获取其加入的团队列表
     * @param userId
     * @return
     */
    List<Team> listMyTeam(Long userId);

    /**
     * 获取推荐团队列表
     * @param current
     * @param size
     * @return
     */
    List<TeamVO> listRcmdTeamVO(long current, long size);

    /**
     * 根据团队列表获取团队视图体列表
     * @param teamList
     * @return
     */
    List<TeamVO> getTeamVOList(List<Team> teamList);

    /**
     * 展示团队成员
     * @param teamId
     * @return
     */
    List<UserDTO> listTeamMember(Long teamId);
}
