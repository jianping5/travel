package com.travel.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.team.TeamQueryRequest;
import com.travel.common.model.vo.TeamVDTO;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 8:27
 */
public interface InnerTeamService extends InnerRcmdService<TeamVDTO> {

    /**
     * 从 ES 中搜索分页的数据
     * @param teamQueryRequest
     * @return
     */
    Page<TeamVDTO> searchFromEs(TeamQueryRequest teamQueryRequest);

}
