package com.travel.team.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.team.model.dto.wall.TeamWallQueryRequest;
import com.travel.team.model.entity.TeamWall;
import com.travel.team.model.vo.TeamWallVO;

/**
* @author jianping5
* @description 针对表【team_wall(团队墙表)】的数据库操作Service
* @createDate 2023-03-22 14:40:38
*/
public interface TeamWallService extends IService<TeamWall> {

    /**
     * 校验 TeamWall
     * @param teamWall
     * @param b
     */
    void validTeamWall(TeamWall teamWall, boolean b);

    /**
     * 获取团队视图体
     * @param teamWall
     * @return
     */
    TeamWallVO getTeamWallVO(TeamWall teamWall);

    /**
     * 获取分页团队视图体
     * @param teamWallPage
     * @return
     */
    Page<TeamWallVO> getTeamWallVOPage(Page<TeamWall> teamWallPage);

    /**
     * 根据请求体获取请求 Wrapper
     * @param teamWallQueryRequest
     * @return
     */
    QueryWrapper<TeamWall> getQueryWrapper(TeamWallQueryRequest teamWallQueryRequest);

    /**
     * 创建团队
     * @param teamWall
     * @return
     */
    TeamWall addTeamWall(TeamWall teamWall);


    /**
     * 解散团队
     * @param teamWall
     * @return
     */
    boolean deleteTeamWall(TeamWall teamWall);


}
