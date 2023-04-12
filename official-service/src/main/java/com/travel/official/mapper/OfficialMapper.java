package com.travel.official.mapper;

import com.travel.official.model.entity.Official;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
* @author jianping5
* @description 针对表【official(官方表)】的数据库操作Mapper
* @createDate 2023-03-22 14:45:55
* @Entity com.travel.official.model.entity.Official
*/
public interface OfficialMapper extends BaseMapper<Official> {

    /**
     * 查询某段时间的官方列表（包括已被删除的数据）
     * @param minUpdateTime
     * @return
     */
    List<Official> listOfficialWithDelete(Date minUpdateTime);
}




