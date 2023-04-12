package com.travel.official.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.exception.ThrowUtils;
import com.travel.official.model.entity.OfficialDetail;
import com.travel.official.model.entity.ResourceDetail;
import com.travel.official.service.ResourceDetailService;
import com.travel.official.mapper.ResourceDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【resource_detail(官方资源详情表)】的数据库操作Service实现
* @createDate 2023-03-22 14:45:55
*/
@Service
public class ResourceDetailServiceImpl extends ServiceImpl<ResourceDetailMapper, ResourceDetail>
    implements ResourceDetailService{

    @Override
    public boolean addResourceDetail(ResourceDetail resourceDetail) {
        // 添加到数据库中
        boolean saveResult = this.save(resourceDetail);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);

        // todo：将前几句话添加到官方资源的 intro 字段中

        return true;
    }

    @Override
    public boolean updateResourceDetail(ResourceDetail resourceDetail) {
        // todo：将前几句话更新到官方资源的 intro 字段中

        // 更新数据库
        boolean updateResult = this.updateById(resourceDetail);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);

        return true;
    }
}




