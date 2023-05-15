package com.travel.travel.mq;


import com.travel.common.service.InnerDataService;
import com.travel.common.service.InnerUserService;
import com.travel.travel.job.UpdateCache;
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

    @Resource
    private UpdateCache updateCache;

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

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.travel"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "cache.travel"
    ))
    public void listenTeamQueue(){
        // todo: 当业务线程发现缓存失效后，由当前线程更新缓存

        updateCache.execute();
    }
}
