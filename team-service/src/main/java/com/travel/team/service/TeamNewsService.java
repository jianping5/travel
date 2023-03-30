package com.travel.team.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.team.model.dto.news.TeamNewsQueryRequest;
import com.travel.team.model.entity.TeamNews;
import com.travel.team.model.vo.TeamNewsVO;

/**
* @author jianping5
* @description 针对表【team_news(团队动态表)】的数据库操作Service
* @createDate 2023-03-22 14:40:38
*/
public interface TeamNewsService extends IService<TeamNews> {

    /**
     * 校验团队动态
     * @param teamNews
     * @param b
     */
    void validTeamNews(TeamNews teamNews, boolean b);

    /**
     * 获取团队动态视图体
     * @param teamNews
     * @return
     */
    TeamNewsVO getTeamNewsVO(TeamNews teamNews);

    /**
     * 获取分页团队视图体
     * @param teamPage
     * @return
     */
    Page<TeamNewsVO> getTeamNewsVOPage(Page<TeamNews> teamPage);

    /**
     * 根据请求体获取请求 Wrapper
     * @param teamNewsQueryRequest
     * @return
     */
    QueryWrapper<TeamNews> getQueryWrapper(TeamNewsQueryRequest teamNewsQueryRequest);

    /**
     * 创建团队
     * @param teamNews
     * @return
     */
    boolean addTeamNews(TeamNews teamNews);


    /**
     * 解散团队
     * @param teamNews
     * @return
     */
    boolean deleteTeamNews(TeamNews teamNews);

}
