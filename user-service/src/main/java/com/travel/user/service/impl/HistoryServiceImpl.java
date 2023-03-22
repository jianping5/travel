package com.travel.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.user.model.entity.History;
import com.travel.user.service.HistoryService;
import com.travel.user.mapper.HistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【history(历史记录表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History>
    implements HistoryService{

}




