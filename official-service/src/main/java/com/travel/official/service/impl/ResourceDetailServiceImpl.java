package com.travel.official.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}




