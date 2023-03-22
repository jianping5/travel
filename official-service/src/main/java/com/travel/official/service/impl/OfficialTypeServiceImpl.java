package com.travel.official.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.official.model.entity.OfficialType;
import com.travel.official.service.OfficialTypeService;
import com.travel.official.mapper.OfficialTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【official_type(官方类型表)】的数据库操作Service实现
* @createDate 2023-03-22 14:45:55
*/
@Service
public class OfficialTypeServiceImpl extends ServiceImpl<OfficialTypeMapper, OfficialType>
    implements OfficialTypeService{

}




