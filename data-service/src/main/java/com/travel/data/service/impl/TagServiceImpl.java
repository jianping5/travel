package com.travel.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.data.model.entity.Tag;
import com.travel.data.service.TagService;
import com.travel.data.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2023-03-22 14:47:05
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




