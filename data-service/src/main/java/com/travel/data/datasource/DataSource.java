package com.travel.data.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.data.model.dto.SearchRequest;

/**
 * 数据源接口（新接入的数据源必须实现）
 *
 * @author jianping5
 * @param <T>
 */
public interface DataSource<T> {

    /**
     * 搜索
     *
     * @param searchRequest
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<T> doSearch(SearchRequest searchRequest, long pageNum, long pageSize);
}
