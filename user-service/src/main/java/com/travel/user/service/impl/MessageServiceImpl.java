package com.travel.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.user.model.dto.MessageVO;
import com.travel.user.model.entity.Message;
import com.travel.user.model.request.MessageQueryRequest;
import com.travel.user.service.MessageService;
import com.travel.user.mapper.MessageMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【message(消息表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{
    @Resource
    private InnerUserService innerUserService;

    @Override
    public MessageVO getMessageDetail(Message message) {
        MessageVO messageVO = MessageVO.objToVo(message);
        HashSet<Long> longSet = new HashSet<>();
        longSet.add(message.getMessageUserId());
        List<UserDTO> userDTOS = innerUserService.listByIds(longSet);
        HashMap<Long, UserDTO> longUserDTOHashMap = new HashMap<>();
        userDTOS.stream().forEach(k->{
            longUserDTOHashMap.put(k.getId(),k);
        });
        //填充消息发布者头像，昵称
        if(longUserDTOHashMap.containsKey(message.getMessageUserId())){
            UserDTO userDTO = longUserDTOHashMap.get(message.getMessageUserId());
            messageVO.setMessageUserName(userDTO.getUserName());
            messageVO.setMessageUserAvatarUrl(userDTO.getUserAvatar());
        }
        return messageVO;
    }

    @Override
    public Page<MessageVO> getMessageVOPage(Page<Message> messagePage) {
        List<Message> messageList = messagePage.getRecords();
        Page<MessageVO> messageVOPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
        if (CollectionUtils.isEmpty(messageList)) {
            return messageVOPage;
        }
        Set<Long> longSet = messageList.stream().map(k -> k.getMessageUserId()).collect(Collectors.toSet());
        List<UserDTO> userDTOS = innerUserService.listByIds(longSet);
        HashMap<Long, UserDTO> longUserDTOHashMap = new HashMap<>();
        userDTOS.stream().forEach(k->{
            longUserDTOHashMap.put(k.getId(),k);
        });
        // 填充信息
        List<MessageVO> messageVOList = messageList.stream().map(message -> {
            MessageVO messageVO = MessageVO.objToVo(message);
            //填充消息发布者头像，昵称
            if(longUserDTOHashMap.containsKey(message.getMessageUserId())){
                UserDTO userDTO = longUserDTOHashMap.get(message.getMessageUserId());
                messageVO.setMessageUserName(userDTO.getUserName());
                messageVO.setMessageUserAvatarUrl(userDTO.getUserAvatar());
            }
            return messageVO;
        }).collect(Collectors.toList());
        messageVOPage.setRecords(messageVOList);
        return messageVOPage;
    }

    @Override
    public QueryWrapper<Message> getQueryWrapper(MessageQueryRequest messageQueryRequest) {

        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        if (messageQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询条件中的字段
        String sortField = messageQueryRequest.getSortField();
        String sortOrder = messageQueryRequest.getSortOrder();
        Long userId = messageQueryRequest.getUserId();

        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        queryWrapper.eq("is_deleted", 0);
        return queryWrapper;
    }
    @Override
    public boolean deleteMessage(Message message) {
        // todo: 考虑加事务
        // 将消息状态设置为已删除
        message.setIsDeleted(1);
        boolean result = this.updateById(message);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }
}




