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
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 6/4/2023 下午 3:43
 */
@RestController
@RequestMapping("/official-notification")
public class NotificationController {
    @Resource
    private NotificationService notificationService;

    /**
     * 添加资讯通知
     *
     * @param notificationAddRequest
     * @return
     */
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
     * 下架官方资源
     *
     * @param deleteRequest
     * @return
     */
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
     * 更新周边
     *
     * @param notificationUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateNotification(@RequestBody NotificationUpdateRequest notificationUpdateRequest) {
        // 校验团队更新请求体
        if (notificationUpdateRequest == null || notificationUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将团队更新请求体的内容赋值给 团队
        Notification Notification = new Notification();
        BeanUtils.copyProperties(notificationUpdateRequest, Notification);

        // 参数校验
        notificationService.validNotification(Notification, false);
        long id = notificationUpdateRequest.getId();

        // 判断是否存在
        Notification oldPost = notificationService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新团队
        notificationService.updateNotification(Notification);

        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取资讯通知详情
     *
     * @param id
     * @return
     */
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
