package com.travel.team.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.team.model.entity.TeamWall;
import com.travel.team.service.TeamWallService;
import com.travel.team.mapper.TeamWallMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【team_wall(团队墙表)】的数据库操作Service实现
* @createDate 2023-03-22 14:40:38
*/
@Service
public class TeamWallServiceImpl extends ServiceImpl<TeamWallMapper, TeamWall>
    implements TeamWallService{

}



