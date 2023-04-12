package com.travel.official.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.official.model.dto.official.OfficialQueryRequest;
import com.travel.official.model.dto.officialApply.OfficialApplyQueryRequest;
import com.travel.official.model.entity.Official;
import com.travel.official.model.entity.OfficialApply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.official.model.vo.OfficialApplyVO;

/**
* @author jianping5
* @description 针对表【official_apply(官方申请表)】的数据库操作Service
* @createDate 2023-03-22 14:45:55
*/
public interface OfficialApplyService extends IService<OfficialApply> {

    /**
     * 验证 officialApply
     * @param officialApply
     */
    void validOfficial(OfficialApply officialApply);

    /**
     * 添加官方申请
     * @param officialApply
     * @return
     */
    Long addOfficialApply(OfficialApply officialApply);

    /**
     * 获取官方申请 VO
     * @param officialApply
     * @return
     */
    OfficialApplyVO getOfficialApplyVO(OfficialApply officialApply);

    /**
     * 根据请求体获取请求 Wrapper
     * @param officialApplyQueryRequest
     * @return
     */
    QueryWrapper<OfficialApply> getQueryWrapper(OfficialApplyQueryRequest officialApplyQueryRequest);

    /**
     * 获取分页官方申请视图体
     * @param officialApplyPage
     * @return
     */
    Page<OfficialApplyVO> getOfficialApplyVOPage(Page<OfficialApply> officialApplyPage);
}
