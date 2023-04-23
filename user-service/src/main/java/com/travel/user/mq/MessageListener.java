package com.travel.user.mq;

import com.google.gson.Gson;
import com.travel.common.model.dto.MessageDTO;
import com.travel.common.model.dto.user.UpdateTokenRequest;
import com.travel.common.service.InnerUserService;
import com.travel.user.model.entity.Collection;
import com.travel.user.model.entity.Follow;
import com.travel.user.model.entity.Message;
import com.travel.user.model.entity.UserLike;
import com.travel.user.model.request.UserLikeRequest;
import com.travel.user.service.CollectionService;
import com.travel.user.service.FollowService;
import com.travel.user.service.MessageService;
import com.travel.user.service.UserLikeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zgy
 * @create 2023-04-14 13:49
 */
@Component
public class MessageListener {
    @Resource
    private FollowService followService;
    @Resource
    private CollectionService collectionService;
    @Resource
    private UserLikeService userLikeService;

    @Resource
    private Gson gson;

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private MessageService messageService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("user.collection.execute"),
            exchange = @Exchange(value = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "user.collection.execute"

    ))
    public void executeCollect(String msg) {
        String[] split = msg.split(",");
        Long userId = Long.parseLong(split[0]);
        Integer requestType = Integer.parseInt(split[1]);
        Long favoriteId = Long.parseLong(split[2]);
        Integer collectionObjType = Integer.parseInt(split[3]);
        Long collectionObjId = Long.parseLong(split[4]);
        Collection collection = new Collection();
        collection.setUserId(userId);
        collection.setCollectionObjType(collectionObjType);
        collection.setCollectionObjId(collectionObjId);
        collection.setFavoriteId(favoriteId);
        collectionService.executeCollection(collection, requestType.equals(1));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("user.follow.execute"),
            exchange = @Exchange(value = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "user.follow.execute"

    ))
    public void executeFollow(String msg) {
        String[] split = msg.split(",");
        Long userId = Long.parseLong(split[0]);
        Long followUserId = Long.parseLong(split[1]);
        Integer followState = Integer.parseInt(split[2]);
        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setFollowUserId(followUserId);
        follow.setFollowState(followState);
        followService.executeFollow(follow);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("user.like.execute"),
            exchange = @Exchange(value = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "user.like.execute"

    ))
    public void executeLike(String msg) {
        UserLikeRequest request = new Gson().fromJson(msg, UserLikeRequest.class);
        UserLike userLike = new UserLike();
        userLike.setUserId(request.getUserId());
        userLike.setLikeObjType(request.getLikeObjType());
        userLike.setLikeObjId(request.getLikeObjId());
        userLike.setLikeState(request.getLikeState());
        userLikeService.executeUserLike(userLike);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.token"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "token.#"
    ))
    public void listenTokenQueue(String msg) {
        // 更新代币
        UpdateTokenRequest updateTokenRequest = gson.fromJson(msg, UpdateTokenRequest.class);
        Integer token = updateTokenRequest.getToken();
        Long userId = updateTokenRequest.getUserId();
        Boolean isAdd = updateTokenRequest.getIsAdd();
        innerUserService.updateToken(userId, token, isAdd);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "travel.message"),
            exchange = @Exchange(name = "travel.topic", type = ExchangeTypes.TOPIC),
            key = "message.#"
    ))
    public void listenMessageQueue(String msg) {
        // todo: 出现异常了怎么办？
        // 获取消息体
        Message message = new Message();
        MessageDTO messageDTO = gson.fromJson(msg, MessageDTO.class);
        BeanUtils.copyProperties(messageDTO, message);
        // 将消息添加到消息表中
        messageService.save(message);
    }
}
