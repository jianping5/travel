package com.travel.official.mq;

import com.google.gson.Gson;
import com.travel.official.job.UpdateCache;
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
            value = @Queue(name = "travel.derivative"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "cache.derivative"
    ))
    public void listenDerivativeQueue(String msg){
        // 当业务线程发现缓存失效后，由当前线程更新缓存

        updateCache.executeDerivative();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.official"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "cache.official"
    ))
    public void listenOfficialQueue(String msg){
        // 当业务线程发现缓存失效后，由当前线程更新缓存
        int typeId = Integer.valueOf(msg);
        updateCache.executeOfficial(typeId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.official.like"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "cache.official.like"
    ))
    public void listenOfficialLikeQueue(String msg){
        // todo: 增加对应点赞量，并更新点赞表
        String[] s = msg.split(" ");
        long loginUserId = Integer.parseInt(s[0]);
        int type = Integer.parseInt(s[1]);
        long id = Integer.parseInt(s[2]);
        int status = Integer.parseInt(s[3]);

        updateCache.executeOfficialLike(loginUserId, type, id, status);
    }

}
