package com.travel.data.datasource;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.official.DerivativeQueryRequest;
import com.travel.common.model.vo.DerivativeVDTO;
import com.travel.common.service.InnerDerivativeService;
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
public class DerivativeDataSource implements DataSource<DerivativeVDTO> {

    @DubboReference
    private InnerDerivativeService innerDerivativeService;

    @Override
    public Page<DerivativeVDTO> doSearch(SearchRequest searchRequest, long pageNum, long pageSize) {
        // 获取周边查询请求
        DerivativeQueryRequest derivativeQueryRequest = new DerivativeQueryRequest();
        derivativeQueryRequest.setSearchText(searchRequest.getSearchText());
        derivativeQueryRequest.setCurrent(pageNum);
        derivativeQueryRequest.setPageSize(pageSize);
        derivativeQueryRequest.setSortField(searchRequest.getSortField());
        derivativeQueryRequest.setSortOrder(searchRequest.getSortOrder());

        return innerDerivativeService.searchFromEs(derivativeQueryRequest);
    }
}
