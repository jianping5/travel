package com.travel.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author zgy
 * @createDate 22/3/2023 下午 3:10
 */
@RestController
@Api(tags = "关注 Controller")
@RequestMapping("/follow")
public class FollowController {
    @Resource
    private FollowService followService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/execute")
    @ApiOperation(value = "关注/取消关注")
    public BaseResponse<Boolean> executeFollow(@RequestBody FollowRequest followRequest) {
        // 校验关注请求体
        if (followRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = UserHolder.getUser();
        followRequest.setUserId(loginUser.getId());

        followService.validFollow(followRequest, true);

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


    @PostMapping("/list/page/vo")
    @ApiOperation(value = "获取关注列表")
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
