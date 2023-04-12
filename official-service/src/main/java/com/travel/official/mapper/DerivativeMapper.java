package com.travel.official.mapper;

import com.travel.official.model.entity.Derivative;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
* @author jianping5
* @description 针对表【derivative(周边表)】的数据库操作Mapper
* @createDate 2023-03-30 16:50:48
* @Entity com.travel.official.model.entity.Derivative
*/
public interface DerivativeMapper extends BaseMapper<Derivative> {

    /**
     * 查询某段时间的周边列表（包括已被删除的数据）
     * @param minUpdateTime
     * @return
     */
    List<Derivative> listDerivativeWithDelete(Date minUpdateTime);
}




