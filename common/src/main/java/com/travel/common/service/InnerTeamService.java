package com.travel.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.TeamQueryRequest;
import com.travel.common.model.dto.TeamDTO;
import com.travel.common.model.vo.TeamVDTO;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 8:27
 */
public interface InnerTeamService {
    String sayHello();

    /**
     * 从 ES 中搜索分页的数据
     * @param teamQueryRequest
     * @return
     */
    Page<TeamDTO> searchFromEs(TeamQueryRequest teamQueryRequest);

    /**
     * 获取分页团队视图体
     * @param teamPage
     * @return
     */
    Page<TeamVDTO> getTeamVOPage(Page<TeamDTO> teamPage);
}
