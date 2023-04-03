package com.travel.team.mapper;

import com.travel.team.model.entity.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
* @author jianping5
* @description 针对表【team(团队表)】的数据库操作Mapper
* @createDate 2023-03-22 14:40:38
* @Entity com.travel.team.model.entity.Team
*/
public interface TeamMapper extends BaseMapper<Team> {


    /**
     * 查询某段时间的团队列表（包括已被删除的数据）
     * @param minUpdateTime
     * @return
     */
    List<Team> listTeamWithDelete(Date minUpdateTime);

}




