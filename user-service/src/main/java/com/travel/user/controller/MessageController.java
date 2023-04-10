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
import com.travel.user.model.dto.MessageVO;
import com.travel.user.model.entity.Message;
import com.travel.user.model.request.MessageQueryRequest;
import com.travel.user.model.request.MessageUpdateRequest;
import com.travel.user.service.MessageService;
import com.travel.user.service.UserService;
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
@RequestMapping("/message")
public class MessageController {
    @Resource()
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;

    @Resource
    private MessageService messageService;



    @PostMapping("/delete")
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
    
    @PostMapping("/update")
    public BaseResponse<Boolean> updateMessage(@RequestBody MessageUpdateRequest messageUpdateRequest) {
        // 校验消息更新请求体
        if (messageUpdateRequest == null || messageUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将消息更新请求体的内容赋值给消息
        Message message = new Message();
        BeanUtils.copyProperties(messageUpdateRequest, message);

        // 参数校验
        messageService.validMessage(message, false);
        long id = messageUpdateRequest.getId();

        // 判断是否存在
        Message oldPost = messageService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新消息
        messageService.updateMessage(message);

        return ResultUtils.success(true);
    }
    
    @GetMapping("/get/vo")
    public BaseResponse<MessageVO> getMessageVOById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Message message = messageService.getById(id);
        if (message == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(messageService.getMessageVO(message));
    }
    
    @PostMapping("/list/page/vo")
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
        Page<Message> messagePage = messageService.page(new Page<>(current, size),
                messageService.getQueryWrapper(messageQueryRequest));

        return ResultUtils.success(messageService.getMessageVOPage(messagePage));
    }
}
