package com.travel.team.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.team.model.entity.Team;
import com.travel.team.service.TeamService;
import com.travel.team.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【team(团队表)】的数据库操作Service实现
* @createDate 2023-03-22 14:40:38
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

}




