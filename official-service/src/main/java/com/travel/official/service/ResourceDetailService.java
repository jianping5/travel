package com.travel.official.service;

import com.travel.official.model.entity.ResourceDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author jianping5
* @description 针对表【resource_detail(官方资源详情表)】的数据库操作Service
* @createDate 2023-03-22 14:45:55
*/
public interface ResourceDetailService extends IService<ResourceDetail> {

    /**
     * 添加官方详情
     * @param resourceDetail
     * @return
     */
    boolean addResourceDetail(ResourceDetail resourceDetail);

    /**
     * 更新官方详情
     * @param resourceDetail
     * @return
     */
    boolean updateResourceDetail(ResourceDetail resourceDetail);
}
