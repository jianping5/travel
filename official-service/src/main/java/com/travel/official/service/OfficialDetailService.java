package com.travel.official.service;

import com.travel.official.model.entity.Official;
import com.travel.official.model.entity.OfficialDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author jianping5
* @description 针对表【official_detail(官方详情表)】的数据库操作Service
* @createDate 2023-03-22 14:45:55
*/
public interface OfficialDetailService extends IService<OfficialDetail> {


    /**
     * 校验官方详情
     * @param officialDetail
     */
    void validOfficialDetail(OfficialDetail officialDetail);

    /**
     * 添加官方详情
     * @param officialDetail
     * @return
     */
    boolean addOfficialDetail(OfficialDetail officialDetail);

    /**
     * 更新官方详情
     * @param officialDetail
     * @return
     */
    boolean updateOfficialDetail(OfficialDetail officialDetail);


}
