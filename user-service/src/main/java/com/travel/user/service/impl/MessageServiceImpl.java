package com.travel.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.user.model.entity.Message;
import com.travel.user.service.MessageService;
import com.travel.user.mapper.MessageMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【message(消息表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

}




