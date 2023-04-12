package com.travel.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.official.DerivativeQueryRequest;
import com.travel.common.model.vo.DerivativeVDTO;

/**
 * @author jianping5
 * @createDate 11/4/2023 下午 4:36
 */
public interface InnerDerivativeService {

    /**
     * 从 ES 中搜索分页的数据
     * @param derivativeQueryRequest
     * @return
     */
    Page<DerivativeVDTO> searchFromEs(DerivativeQueryRequest derivativeQueryRequest);
}
