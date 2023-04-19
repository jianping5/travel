package com.travel.travel.mq;


import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.travel.common.service.InnerDataService;
import com.travel.common.service.InnerUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zgy
 * @create 2023-04-14 13:49
 */
@Component
public class MessageListener {
    @Resource
    private InnerUserService innerUserService;
    @DubboReference
    private InnerDataService innerDataService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("user.history.execute"),
            exchange = @Exchange(value = "travel.topic",type = ExchangeTypes.TOPIC),
            key = "user.history.execute"

    ))
    public void executeHistory(String msg){
        String[] split = msg.split(",");
        Long userId = Long.parseLong(split[0]);
        Integer type = Integer.parseInt(split[1]);
        Long id = Long.parseLong(split[2]);
        innerUserService.addHistory(userId,type,id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("user.behavior.execute"),
            exchange = @Exchange(value = "travel.topic",type = ExchangeTypes.TOPIC),
            key = "user.behavior.execute"

    ))
    public void executeBehavior(String msg){
        String[] split = msg.split(",");
        Long userId = Long.parseLong(split[0]);
        Integer type = Integer.parseInt(split[1]);
        Long id = Long.parseLong(split[2]);
        Integer behaviorType = Integer.parseInt(split[3]);
        innerDataService.addBehavior(userId,type,id,behaviorType);
    }
}
