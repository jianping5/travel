package com.travel.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.official.OfficialQueryRequest;
import com.travel.common.model.vo.OfficialVDTO;

import java.util.List;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 8:58
 */
public interface InnerOfficialService extends InnerRcmdService<OfficialVDTO> {

    /**
     * 从 ES 中搜索分页的数据
     * @param officialQueryRequest
     * @return
     */
    Page<OfficialVDTO> searchFromEs(OfficialQueryRequest officialQueryRequest);

    /**
     * 从 ES 中搜索附近的官方推荐
     * @param officialQueryRequest
     * @return
     */
    Page<OfficialVDTO> searchRcmdFromEs(OfficialQueryRequest officialQueryRequest);

    /**
     * 根据用户 id 获取 [周边 id 列表]
     * @param userId
     * @return
     */
    List<Long> listDerivativeId(Long userId);
}
