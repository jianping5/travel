package com.travel.official.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.official.model.dto.notification.NotificationQueryRequest;
import com.travel.official.model.entity.Notification;
import com.travel.official.model.vo.NotificationVO;

import java.util.List;

/**
* @author jianping5
* @description 针对表【notification(资讯通知表)】的数据库操作Service
* @createDate 2023-04-06 15:41:30
*/
public interface NotificationService extends IService<Notification> {

    /**
     * 校验 官方介绍
     * @param notification
     * @param b
     */
    void validNotification(Notification notification, boolean b);

    /**
     * 添加官方介绍
     * @param notification
     * @return
     */
    Long addNotification(Notification notification);


    /**
     * 下架官方资源
     * @param notification
     * @return
     */
    boolean deleteNotification(Notification notification);

    /**
     * 更新官方
     * @param notification
     * @return
     */
    boolean updateNotification(Notification notification);

    /**
     * 获取官方视图体
     * @param notification
     * @return
     */
    NotificationVO getNotificationVO(Notification notification);

    /**
     * 获取分页官方视图体
     * @param notificationPage
     * @return
     */
    Page<NotificationVO> getNotificationVOPage(Page<Notification> notificationPage);

    /**
     * 根据请求体获取请求 Wrapper
     * @param notificationQueryRequest
     * @return
     */
    QueryWrapper<Notification> getQueryWrapper(NotificationQueryRequest notificationQueryRequest);

    /**
     * 根据官方资源列表获取官方资源视图体列表
     * @param notificationList
     * @return
     */
    List<NotificationVO> getNotificationVOList(List<Notification> notificationList);

}
