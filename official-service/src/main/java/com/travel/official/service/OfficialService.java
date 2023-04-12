package com.travel.official.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.official.model.dto.official.OfficialQueryRequest;
import com.travel.official.model.entity.Official;
import com.travel.official.model.vo.OfficialVO;

import java.util.List;

/**
* @author jianping5
* @description 针对表【official(官方表)】的数据库操作Service
* @createDate 2023-03-22 14:45:55
*/
public interface OfficialService extends IService<Official> {
    /**
     * 校验 官方介绍
     * @param official
     * @param b
     */
    void validOfficial(Official official, boolean b);

    /**
     * 添加官方介绍
     * @param official
     * @return
     */
    Long addOfficial(Official official);


    /**
     * 更新官方
     * @param official
     * @return
     */
    boolean updateOfficial(Official official);

    /**
     * 获取官方视图体
     * @param official
     * @return
     */
    OfficialVO getOfficialVO(Official official);

    /**
     * 获取分页官方视图体
     * @param officialPage
     * @return
     */
    Page<OfficialVO> getOfficialVOPage(Page<Official> officialPage);

    /**
     * 根据请求体获取请求 Wrapper
     * @param officialQueryRequest
     * @return
     */
    QueryWrapper<Official> getQueryWrapper(OfficialQueryRequest officialQueryRequest);

    /**
     * 获取推荐官方（指定类型）
     * @param current
     * @param size
     * @param typeId
     * @return
     */
    List<OfficialVO> listRcmdOfficialVO(long current, long size, int typeId);

    /**
     * 获取官方详情视图体
     * @param official
     * @return
     */
    OfficialVO getOfficialDetail(Official official);

    /**
     * 根据官方列表获取官方视图体列表（批量查询快于多次单个查询）
     * @param officialList
     * @return
     */
    List<OfficialVO> getOfficialVOList(List<Official> officialList);

    /**
     * 点赞
     * @param id
     * @param type
     * @param status
     * @return
     */
    boolean likeOfficial(Long id, Integer type, Integer status);
}
