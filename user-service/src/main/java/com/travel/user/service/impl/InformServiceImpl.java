package com.travel.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.user.model.entity.Inform;
import com.travel.user.service.InformService;
import com.travel.user.mapper.InformMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【inform(举报表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class InformServiceImpl extends ServiceImpl<InformMapper, Inform>
    implements InformService{

}




