package com.travel.data.mq;

import com.google.gson.Gson;
import com.travel.common.constant.BehaviorTypeConstant;
import com.travel.common.model.dto.TagAddRequest;
import com.travel.data.model.entity.Behavior;
import com.travel.data.service.BehaviorService;
import com.travel.data.service.TagService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 12/4/2023 下午 3:49
 */
@Component
public class MessageListener {

    @Resource
    private BehaviorService behaviorService;

    @Resource
    private TagService tagService;

    @Resource
    private Gson gson;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.behavior"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "behavior.#"
    ))
    public void listenBehaviorQueue(String msg){
        // todo: 增加行为记录
        // 解析消息
        String[] s = msg.split(" ");
        long loginUserId = Integer.parseInt(s[0]);
        int type = Integer.parseInt(s[1]);
        long id = Integer.parseInt(s[2]);
        BehaviorTypeConstant behaviorTypeConstant = BehaviorTypeConstant.getEnumByValue(s[3]);

        // 初始化 behavior
        Behavior behavior = new Behavior();
        behavior.setUserId(loginUserId);
        behavior.setBehaviorObjType(type);
        behavior.setBehaviorObjId(id);
        behavior.setBehaviorType(behaviorTypeConstant.getTypeIndex());

        // todo：添加消息记录（失败了怎么处理？）
        behaviorService.save(behavior);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.tag"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "tag.#"
    ))
    public void listenTagQueue(String msg){
        TagAddRequest tagAddRequest = gson.fromJson(msg, TagAddRequest.class);

        // todo：添加消息记录（失败了怎么处理？）
        tagService.addCustomizedTag(tagAddRequest);
    }
}
