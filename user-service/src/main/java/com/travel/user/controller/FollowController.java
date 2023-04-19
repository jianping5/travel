package com.travel.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.user.model.dto.FollowVO;
import com.travel.user.model.entity.Follow;
import com.travel.user.model.request.FollowRequest;
import com.travel.user.model.request.FollowQueryRequest;
import com.travel.user.service.FollowService;
import org.redisson.api.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author zgy
 * @createDate 22/3/2023 下午 3:10
 */
@RestController
@RequestMapping("/follow")
public class FollowController {
    @Resource
    private FollowService followService;
    @Resource()
    private RedissonClient redissonClient;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/follow/execute")
    public BaseResponse<Boolean> executeFollow(@RequestBody FollowRequest followRequest) {
        // 校验关注请求体
        if (followRequest == null || followRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        followService.validFollow(followRequest, true);
        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可更改
        if (!followRequest.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 定义交换机名称
        String exchangeName = "travel.topic";
        String message = null;

        //获取关注实体
        RSet<Long> list = redissonClient.getSet("travel:user:follow:" + followRequest.getFollowUserId());
        boolean contains = list.contains(followRequest.getUserId());
        if(contains&&followRequest.getFollowState().equals(0)){
            list.remove(followRequest.getUserId());
            // 发送消息，让对应线程将数据写入数据库
            message = ""+followRequest.getUserId()+","+followRequest.getFollowUserId()+","+followRequest.getFollowState();
            rabbitTemplate.convertAndSend(exchangeName, "user.follow.execute", message);

        }else if(!contains&&followRequest.getFollowState().equals(1)){
            list.add(followRequest.getUserId());
            // 发送消息，让对应线程将数据写入数据库
            message = ""+followRequest.getUserId()+","+followRequest.getFollowUserId()+","+followRequest.getFollowState();
            rabbitTemplate.convertAndSend(exchangeName, "user.follow.execute", message);
        }
        return ResultUtils.success(true);
    }


    @PostMapping("/follow/list/page/vo")
    public BaseResponse<Page<FollowVO>> listFollowVOByPage(@RequestBody FollowQueryRequest followQueryRequest) {
        if (followQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long size = followQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Follow> followPage = followService.queryFollow(followQueryRequest);
        return ResultUtils.success(followService.getFollowVOPage(followPage));
    }

}
