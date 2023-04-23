package com.travel.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.user.model.dto.CollectionVO;
import com.travel.user.model.entity.Collection;
import com.travel.user.model.request.CollectionQueryRequest;
import com.travel.user.model.request.CollectionRequest;
import com.travel.user.service.CollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zgy
 * @create 2023-04-17 15:07
 */
@RestController
@Api(tags = "收藏 Controller")
@RequestMapping("/collection")
public class CollectionController {
    @Resource
    private CollectionService collectionService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "收藏/取消收藏")
    @PostMapping("/collection/execute")
    public BaseResponse<Boolean> executeCollection(@RequestBody CollectionRequest collectionRequest) {
        // 校验关注请求体
        if (collectionRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = UserHolder.getUser();
        collectionRequest.setUserId(loginUser.getId());

        collectionService.validCollection(collectionRequest, true);


        // 仅本人或管理员可更改
        if (!collectionRequest.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 定义交换机名称
        String exchangeName = "travel.topic";
        String message = null;

        //获取关注实体
        RSet<Long> list = redissonClient.getSet("travel:user:collection:" + collectionRequest.getCollectionObjType()+":"+collectionRequest.getCollectionObjId());
        boolean contains = list.contains(collectionRequest.getUserId());
        if(contains&&collectionRequest.getRequestType().equals(0)){
            list.remove(collectionRequest.getUserId());
            // 发送消息，让对应线程将数据写入数据库
            message = ""+collectionRequest.getUserId()+","+collectionRequest.getRequestType()+","+collectionRequest.getFavoriteId()+","+collectionRequest.getCollectionObjType()+","+collectionRequest.getCollectionObjId();
            rabbitTemplate.convertAndSend(exchangeName, "user.collection.execute", message);
        }else if(!contains&&collectionRequest.getRequestType().equals(1)){
            list.add(collectionRequest.getUserId());
            // 发送消息，让对应线程将数据写入数据库
            message = ""+collectionRequest.getUserId()+","+collectionRequest.getRequestType()+","+collectionRequest.getFavoriteId()+","+collectionRequest.getCollectionObjType()+","+collectionRequest.getCollectionObjId();
            rabbitTemplate.convertAndSend(exchangeName, "user.collection.execute", message);
        }
        return ResultUtils.success(true);
    }

    @ApiOperation(value = "获取收藏列表")
    @PostMapping("/collection/list/page/vo")
    public BaseResponse<Page<CollectionVO>> listCollectionVOByPage(@RequestBody CollectionQueryRequest collectionQueryRequest) {
        if (collectionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = UserHolder.getUser();
        collectionQueryRequest.setUserId(user.getId());
        long size = collectionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Collection> collectionPage = collectionService.queryCollection(collectionQueryRequest);
        return ResultUtils.success(collectionService.getCollectionVOPage(collectionPage));
    }

}
