package com.travel.data.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.team.TeamQueryRequest;
import com.travel.common.model.vo.TeamVDTO;
import com.travel.common.service.InnerTeamService;
import com.travel.data.model.dto.SearchRequest;
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
    public Page<TeamVDTO> doSearch(SearchRequest searchRequest, long pageNum, long pageSize) {
        // 获取团队查询请求
        TeamQueryRequest teamQueryRequest = new TeamQueryRequest();
        teamQueryRequest.setSearchText(searchRequest.getSearchText());
        teamQueryRequest.setCurrent(pageNum);
        teamQueryRequest.setPageSize(pageSize);
        teamQueryRequest.setSortField(searchRequest.getSortField());
        teamQueryRequest.setSortOrder(searchRequest.getSortOrder());

        return innerTeamService.searchFromEs(teamQueryRequest);
    }
}




