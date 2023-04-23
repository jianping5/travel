package com.travel.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.user.model.dto.MessageVO;
import com.travel.user.model.entity.Message;
import com.travel.user.model.request.MessageQueryRequest;
import com.travel.user.model.request.MessageUpdateRequest;
import com.travel.user.service.MessageService;
import com.travel.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author zgy
 * @create 2023-04-03 21:58
 */
@Slf4j
@RestController
@Api(tags = "消息 Controller")
@RequestMapping("/message")
public class MessageController {
    @Resource()
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;

    @Resource
    private MessageService messageService;

    @PostMapping("/delete")
    @ApiOperation(value = "删除消息")
    public BaseResponse<Boolean> deleteMessage(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 Message id
        long id = deleteRequest.getId();

        // 判断是否存在
        Message oldPost = messageService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(2)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = messageService.deleteMessage(oldPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }
    
    @GetMapping("/get/detail")
    @ApiOperation(value = "根据id获取消息详情")
    public BaseResponse<MessageVO> getMessageDetailById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Message message = messageService.getById(id);

        //更新消息的阅读状态
        if (message == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }else{
            // 判断当前用户是否为当前消息的创建人
            User loginUser = UserHolder.getUser();
            Long loginUserId = loginUser.getId();
            Long messageUserId = message.getUserId();
            if(loginUserId.equals(messageUserId)){
                message.setMessageState(1);
                messageService.updateById(message);
            }
        }
        return ResultUtils.success(messageService.getMessageDetail(message));
    }
    
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "获取消息列表")
    public BaseResponse<Page<MessageVO>> listMessageVOByPage(@RequestBody MessageQueryRequest messageQueryRequest) {
        if (messageQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取当前登录用户id
        messageQueryRequest.setUserId(UserHolder.getUser().getId());
        long current = messageQueryRequest.getCurrent();
        long size = messageQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Message> queryWrapper = messageService.getQueryWrapper(messageQueryRequest);
        Page<Message> messagePage = messageService.page(new Page<>(current, size),queryWrapper);
        return ResultUtils.success(messageService.getMessageVOPage(messagePage));
    }
}
