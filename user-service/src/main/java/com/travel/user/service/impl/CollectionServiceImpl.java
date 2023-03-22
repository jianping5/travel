package com.travel.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.user.model.entity.Collection;
import com.travel.user.service.CollectionService;
import com.travel.user.mapper.CollectionMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【collection(收藏表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection>
    implements CollectionService{

}




