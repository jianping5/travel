package com.travel.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.data.model.entity.Behavior;
import com.travel.data.service.BehaviorService;
import com.travel.data.mapper.BehaviorMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【behavior(用户行为表)】的数据库操作Service实现
* @createDate 2023-03-22 14:47:05
*/
@Service
public class BehaviorServiceImpl extends ServiceImpl<BehaviorMapper, Behavior>
    implements BehaviorService{

}




