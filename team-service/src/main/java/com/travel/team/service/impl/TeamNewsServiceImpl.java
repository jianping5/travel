package com.travel.team.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.team.model.entity.TeamNews;
import com.travel.team.service.TeamNewsService;
import com.travel.team.mapper.TeamNewsMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【team_news(团队动态表)】的数据库操作Service实现
* @createDate 2023-03-22 14:40:38
*/
@Service
public class TeamNewsServiceImpl extends ServiceImpl<TeamNewsMapper, TeamNews>
    implements TeamNewsService{

}




