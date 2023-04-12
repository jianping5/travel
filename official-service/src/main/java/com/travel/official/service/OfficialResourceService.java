package com.travel.official.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.official.model.dto.officialResource.OfficialResourceQueryRequest;
import com.travel.official.model.entity.OfficialResource;
import com.travel.official.model.vo.OfficialResourceVO;

import java.util.List;

/**
* @author jianping5
* @description 针对表【officialResource_resource(官方资源表)】的数据库操作Service
* @createDate 2023-03-22 14:45:55
*/
public interface OfficialResourceService extends IService<OfficialResource> {

    /**
     * 校验 官方介绍
     * @param officialResource
     * @param b
     */
    void validOfficialResource(OfficialResource officialResource, boolean b);

    /**
     * 添加官方介绍
     * @param officialResource
     * @return
     */
    Long addOfficialResource(OfficialResource officialResource);


    /**
     * 下架官方资源
     * @param officialResource
     * @return
     */
    boolean deleteOfficialResource(OfficialResource officialResource);

    /**
     * 更新官方
     * @param officialResource
     * @return
     */
    boolean updateOfficialResource(OfficialResource officialResource);

    /**
     * 获取官方视图体
     * @param officialResource
     * @return
     */
    OfficialResourceVO getOfficialResourceVO(OfficialResource officialResource);

    /**
     * 获取分页官方视图体
     * @param officialResourcePage
     * @return
     */
    Page<OfficialResourceVO> getOfficialResourceVOPage(Page<OfficialResource> officialResourcePage);

    /**
     * 根据请求体获取请求 Wrapper
     * @param officialResourceQueryRequest
     * @return
     */
    QueryWrapper<OfficialResource> getQueryWrapper(OfficialResourceQueryRequest officialResourceQueryRequest);

    /**
     * 根据官方资源获取官方资源详情视图体
     * @param officialResource
     * @return
     */
    OfficialResourceVO getOfficialDetail(OfficialResource officialResource);

    /**
     * 根据官方资源列表获取官方资源视图体列表
     * @param officialResourceList
     * @return
     */
    List<OfficialResourceVO> getOfficialResourceVOList(List<OfficialResource> officialResourceList);
}
