package com.travel.team.mq;

import com.google.gson.Gson;
import com.travel.team.job.UpdateCache;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
    private UpdateCache updateCache;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.team"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "cache.team"
    ))
    public void listenTeamQueue(String msg){
        // todo: 当业务线程发现缓存失效后，由当前线程更新缓存

        updateCache.execute();
    }
}
