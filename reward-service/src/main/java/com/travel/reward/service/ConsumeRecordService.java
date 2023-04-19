package com.travel.reward.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.reward.model.dto.ConsumeRecordQueryRequest;
import com.travel.reward.model.entity.ConsumeRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.reward.model.vo.ConsumeRecordVO;

/**
* @author jianping5
* @description 针对表【consume_record(消费记录表)】的数据库操作Service
* @createDate 2023-03-22 14:41:35
*/
public interface ConsumeRecordService extends IService<ConsumeRecord> {

    /**
     * 根据请求体获取请求 Wrapper
     * @param consumeRecordQueryRequest
     * @return
     */
    QueryWrapper<ConsumeRecord> getQueryWrapper(ConsumeRecordQueryRequest consumeRecordQueryRequest);

    /**
     * 获取消费分页视图体
     * @param consumeRecordPage
     * @return
     */
    Page<ConsumeRecordVO> getConsumeVOPage(Page<ConsumeRecord> consumeRecordPage);
}
