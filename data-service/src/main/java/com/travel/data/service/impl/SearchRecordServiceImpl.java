package com.travel.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.data.model.entity.SearchRecord;
import com.travel.data.service.SearchRecordService;
import com.travel.data.mapper.SearchRecordMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【search_record(检索记录表)】的数据库操作Service实现
* @createDate 2023-03-22 14:47:05
*/
@Service
public class SearchRecordServiceImpl extends ServiceImpl<SearchRecordMapper, SearchRecord>
    implements SearchRecordService{

}




