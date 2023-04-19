package com.travel.data.datasource;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.official.OfficialQueryRequest;
import com.travel.common.model.vo.OfficialVDTO;
import com.travel.common.service.InnerOfficialService;
import com.travel.data.model.dto.OfficialRcmdRequest;
import com.travel.data.model.dto.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @author jianping5
 * @createDate 4/4/2023 下午 6:44
 */
@Service
@Slf4j
public class OfficialDataSource implements DataSource<OfficialVDTO> {

    @DubboReference
    private InnerOfficialService innerOfficialService;

    @Override
    public Page<OfficialVDTO> doSearch(SearchRequest searchRequest, long pageNum, long pageSize) {
        // 获取官方查询请求
        OfficialQueryRequest officialQueryRequest = new OfficialQueryRequest();
        officialQueryRequest.setSearchText(searchRequest.getSearchText());
        officialQueryRequest.setCurrent(pageNum);
        officialQueryRequest.setPageSize(pageSize);
        officialQueryRequest.setSortField(searchRequest.getSortField());
        officialQueryRequest.setSortOrder(searchRequest.getSortOrder());
        officialQueryRequest.setTypeId(searchRequest.getClassifyTypeId());

        return innerOfficialService.searchFromEs(officialQueryRequest);
    }

    /**
     * 获取景区推荐酒店/美食
     * @param officialRcmdRequest
     * @return
     */
    public Page<OfficialVDTO> doRcmd(OfficialRcmdRequest officialRcmdRequest) {
        // 获取官方推荐请求
        OfficialQueryRequest officialQueryRequest = new OfficialQueryRequest();
        officialQueryRequest.setId(officialRcmdRequest.getOfficialId());
        officialQueryRequest.setTypeId(officialRcmdRequest.getRcmdType());
        officialQueryRequest.setCurrent(officialRcmdRequest.getCurrent());
        officialQueryRequest.setPageSize(officialRcmdRequest.getPageSize());
        officialQueryRequest.setLatAndLong(officialRcmdRequest.getLatAndLong());
        officialQueryRequest.setSortField(officialRcmdRequest.getSortField());
        officialQueryRequest.setSortOrder(officialRcmdRequest.getSortOrder());

        return innerOfficialService.searchRcmdFromEs(officialQueryRequest);
    }
}
