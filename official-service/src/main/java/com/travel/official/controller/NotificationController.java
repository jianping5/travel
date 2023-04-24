package com.travel.official.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.official.model.dto.notification.NotificationAddRequest;
import com.travel.official.model.dto.notification.NotificationQueryRequest;
import com.travel.official.model.dto.notification.NotificationUpdateRequest;
import com.travel.official.model.entity.Notification;
import com.travel.official.model.vo.NotificationVO;
import com.travel.official.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 6/4/2023 下午 3:43
 */
@RestController
@RequestMapping("/notification")
@Api(tags = "资讯通知 Controller")
public class NotificationController {
    @Resource
    private NotificationService notificationService;

    /**
     * 添加资讯通知
     *
     * @param notificationAddRequest
     * @return
     */
    @ApiOperation(value = "添加资讯通知")
    @PostMapping("/add")
    public BaseResponse<Long> addNotification(@RequestBody NotificationAddRequest notificationAddRequest) {
        // 校验请求体
        if (notificationAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 Notification 中
        Notification notification = new Notification();
        BeanUtils.copyProperties(notificationAddRequest, notification);

        // 校验 Notification 信息是否合法
        notificationService.validNotification(notification, true);

        // 添加官方资源，并返回 id
        return ResultUtils.success(notificationService.addNotification(notification));
    }

    /**
     * 下架资讯通知
     *
     * @param deleteRequest
     * @return
     */
    @ApiOperation(value = "下架资讯通知")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteNotification(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 Notification id
        long id = deleteRequest.getId();

        // 判断是否存在
        Notification oldNotification = notificationService.getById(id);
        ThrowUtils.throwIf(oldNotification == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!oldNotification.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(2)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = notificationService.deleteNotification(oldNotification);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    /**
     * 更新资讯通知
     *
     * @param notificationUpdateRequest
     * @return
     */
    @ApiOperation(value = "更新资讯通知")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateNotification(@RequestBody NotificationUpdateRequest notificationUpdateRequest) {
        // 校验资讯通知更新请求体
        if (notificationUpdateRequest == null || notificationUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将团队更新请求体的内容赋值给 资讯通知
        Notification notification = new Notification();
        BeanUtils.copyProperties(notificationUpdateRequest, notification);

        // 参数校验
        notificationService.validNotification(notification, false);
        long id = notificationUpdateRequest.getId();

        // 判断是否存在
        Notification oldNotification = notificationService.getById(id);
        ThrowUtils.throwIf(oldNotification == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新资讯通知
        User loginUser = UserHolder.getUser();
        notification.setUserId(loginUser.getId());
        notificationService.updateNotification(notification);

        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取资讯通知详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据 id 获取资讯通知详情")
    @GetMapping("/get/vo")
    public BaseResponse<NotificationVO> getNotificationVOById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Notification notification = notificationService.getById(id);
        if (notification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(notificationService.getNotificationVO(notification));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param notificationQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取列表（封装类）")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<NotificationVO>> listNotificationVOByPage(@RequestBody NotificationQueryRequest notificationQueryRequest) {
        if (notificationQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = notificationQueryRequest.getCurrent();
        long size = notificationQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Notification> notificationPage = notificationService.page(new Page<>(current, size),
                notificationService.getQueryWrapper(notificationQueryRequest));

        return ResultUtils.success(notificationService.getNotificationVOPage(notificationPage));
    }
    
}
