package com.travel.data.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.TeamQueryRequest;
import com.travel.common.model.dto.TeamDTO;
import com.travel.common.model.vo.TeamVDTO;
import com.travel.common.service.InnerTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 团队服务实现
 *
 * @author jianping5
 */
@Service
@Slf4j
public class TeamDataSource implements DataSource<TeamVDTO> {

    @DubboReference
    private InnerTeamService innerTeamService;

    @Override
    public Page<TeamVDTO> doSearch(String searchText, long pageNum, long pageSize) {
        // 获取团队查询请求
        TeamQueryRequest teamQueryRequest = new TeamQueryRequest();
        teamQueryRequest.setSearchText(searchText);
        teamQueryRequest.setCurrent(pageNum);
        teamQueryRequest.setPageSize(pageSize);

        Page<TeamDTO> teamPage = innerTeamService.searchFromEs(teamQueryRequest);
        return innerTeamService.getTeamVOPage(teamPage);
    }
}




