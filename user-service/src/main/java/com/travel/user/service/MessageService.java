package com.travel.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.user.model.dto.MessageVO;
import com.travel.user.model.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.user.model.request.MessageQueryRequest;

/**
* @author jianping5
* @description 针对表【message(消息表)】的数据库操作Service
* @createDate 2023-03-22 14:34:09
*/
public interface MessageService extends IService<Message> {

    
    MessageVO getMessageDetail(Message message);

    Page<MessageVO> getMessageVOPage(Page<Message> messagePage);
    
    QueryWrapper<Message> getQueryWrapper(MessageQueryRequest messageQueryRequest);

    boolean deleteMessage(Message message);

}
