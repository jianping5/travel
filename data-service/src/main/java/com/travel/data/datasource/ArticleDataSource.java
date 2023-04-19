package com.travel.data.datasource;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.travel.ArticleQueryRequest;
import com.travel.common.model.vo.ArticleVDTO;
import com.travel.common.service.InnerTravelService;
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
public class ArticleDataSource implements DataSource<ArticleVDTO> {

    @DubboReference
    private InnerTravelService innerTravelService;

    @Override
    public Page<ArticleVDTO> doSearch(SearchRequest searchRequest, long pageNum, long pageSize) {
        // 获取文章查询请求
        ArticleQueryRequest articleQueryRequest = new ArticleQueryRequest();
        articleQueryRequest.setSearchText(searchRequest.getSearchText());
        articleQueryRequest.setCurrent(pageNum);
        articleQueryRequest.setPageSize(pageSize);
        articleQueryRequest.setSortField(searchRequest.getSortField());
        articleQueryRequest.setSortOrder(searchRequest.getSortOrder());

        return innerTravelService.searchFromEs(articleQueryRequest);
    }

}
