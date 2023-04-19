package com.travel.reward.mq;

import com.google.gson.Gson;
import com.travel.common.model.dto.reward.ConsumeRecordAddRequest;
import com.travel.common.model.dto.reward.ExchangeRecordAddRequest;
import com.travel.reward.model.entity.ConsumeRecord;
import com.travel.reward.model.entity.ExchangeRecord;
import com.travel.reward.service.ConsumeRecordService;
import com.travel.reward.service.ExchangeRecordService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 29/3/2023 下午 8:11
 */
@Component
public class MessageListener {

    @Resource
    private Gson gson;

    @Resource
    private ConsumeRecordService consumeRecordService;

    @Resource
    private ExchangeRecordService exchangeRecordService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.exchange"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "exchange.#"
    ))
    public void listenExchangeLikeQueue(String msg){
        // todo: 添加兑换记录
        ExchangeRecordAddRequest exchangeRecordAddRequest = gson.fromJson(msg, ExchangeRecordAddRequest.class);
        ExchangeRecord exchangeRecord = new ExchangeRecord();
        BeanUtils.copyProperties(exchangeRecordAddRequest, exchangeRecord);
        // todo：添加失败了怎么解决？
        exchangeRecordService.save(exchangeRecord);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.consume"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "consume.#"
    ))
    public void listenConsumeQueue(String msg){
        // todo: 添加消费记录
        ConsumeRecordAddRequest consumeRecordAddRequest = gson.fromJson(msg, ConsumeRecordAddRequest.class);
        ConsumeRecord consumeRecord = new ConsumeRecord();
        BeanUtils.copyProperties(consumeRecordAddRequest, consumeRecord);
        // todo：添加失败了怎么解决？
        consumeRecordService.save(consumeRecord);
    }

}
