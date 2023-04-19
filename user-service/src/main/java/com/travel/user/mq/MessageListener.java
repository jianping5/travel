package com.travel.user.mq;

import com.google.gson.Gson;
import com.travel.common.model.dto.MessageDTO;
import com.travel.common.model.dto.user.UpdateTokenRequest;
import com.travel.user.model.entity.Message;
import com.travel.user.service.MessageService;
import com.travel.user.service.inner.InnerUserServiceImpl;
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
 * @createDate 16/4/2023 下午 8:04
 */
@Component
public class MessageListener {

    @Resource
    private Gson gson;

    @Resource
    private InnerUserServiceImpl innerUserService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.token"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "token.#"
    ))
    public void listenTokenQueue(String msg){
        // 更新代币
        UpdateTokenRequest updateTokenRequest = gson.fromJson(msg, UpdateTokenRequest.class);
        Integer token = updateTokenRequest.getToken();
        Long userId = updateTokenRequest.getUserId();
        Boolean isAdd = updateTokenRequest.getIsAdd();
        innerUserService.updateToken(userId, token, isAdd);
    }

    @Resource
    private MessageService messageService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.message"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "message.#"
    ))
    public void listenMessageQueue(String msg){
        // todo: 出现异常了怎么办？
        // 获取消息体
        Message message = new Message();
        MessageDTO messageDTO = gson.fromJson(msg, MessageDTO.class);
        BeanUtils.copyProperties(messageDTO, message);
        // 将消息添加到消息表中
        messageService.save(message);
    }
}
