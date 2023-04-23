package com.travel.user.controller;

/**
 * @author zgy
 * @create 2023-04-17 15:41
 */

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.user.model.request.UserLikeRequest;
import com.travel.user.service.UserLikeService;
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
@Api(tags = "点赞 Controller")
@RequestMapping("/like")
public class UserLikeController {
    @Resource
    private UserLikeService userLikeService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/execute")
    @ApiOperation("点赞/取消点赞")
    public BaseResponse<Boolean> executeUserLike(@RequestBody UserLikeRequest userLikeRequest) {
        // 校验关注请求体
        if (userLikeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = UserHolder.getUser();
        userLikeRequest.setUserId(loginUser.getId());

        userLikeService.validUserLike(userLikeRequest, true);

        // 仅本人或管理员可更改
        if (!userLikeRequest.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 定义交换机名称
        String exchangeName = "travel.topic";
        String message = new Gson().toJson(userLikeRequest);

        //获取关注实体
        RSet<Long> list = redissonClient.getSet("travel:user:like:" + userLikeRequest.getLikeObjType()+":"+userLikeRequest.getLikeObjId());
        boolean contains = list.contains(userLikeRequest.getUserId());
        if(contains&&userLikeRequest.getLikeState().equals(0)){
            list.remove(userLikeRequest.getUserId());
            // 发送消息，让对应线程将数据写入数据库
            rabbitTemplate.convertAndSend(exchangeName, "user.like.execute", message);
        }else if(!contains&&userLikeRequest.getLikeState().equals(1)){
            list.add(userLikeRequest.getUserId());
            // 发送消息，让对应线程将数据写入数据库
            rabbitTemplate.convertAndSend(exchangeName, "user.like.execute", message);
        }
        return ResultUtils.success(true);
    }

}
