package com.travel.reward.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.reward.model.dto.ExchangeRecordQueryRequest;
import com.travel.reward.model.entity.ExchangeRecord;
import com.travel.reward.model.vo.ExchangeRecordVO;

/**
* @author jianping5
* @description 针对表【exchange_record(兑换记录表)】的数据库操作Service
* @createDate 2023-03-22 14:41:35
*/
public interface ExchangeRecordService extends IService<ExchangeRecord> {

    /**
     * 根据请求体获取请求 Wrapper
     * @param exchangeRecordQueryRequest
     * @return
     */
    QueryWrapper<ExchangeRecord> getQueryWrapper(ExchangeRecordQueryRequest exchangeRecordQueryRequest);

    /**
     * 获取消费分页视图体
     * @param exchangeRecordPage
     * @return
     */
    Page<ExchangeRecordVO> getExchangeVOPage(Page<ExchangeRecord> exchangeRecordPage);

}
