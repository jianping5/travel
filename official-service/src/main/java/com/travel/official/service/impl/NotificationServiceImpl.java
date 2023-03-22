package com.travel.official.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.official.model.entity.Notification;
import com.travel.official.service.NotificationService;
import com.travel.official.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【notification(资讯通知表)】的数据库操作Service实现
* @createDate 2023-03-22 14:45:55
*/
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
    implements NotificationService{

}




