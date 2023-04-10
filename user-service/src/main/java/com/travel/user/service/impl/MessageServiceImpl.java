package com.travel.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
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

import java.util.List;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【message(消息表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

    @Override
    public void validMessage(Message message, boolean add) {
        if (message == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Integer readState = message.getMessageState();

        // 有参数则校验
        if (readState == null||(readState!=0&&readState!= 1)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    @Override
    public MessageVO getMessageVO(Message message) {
        MessageVO messageVO = MessageVO.objToVo(message);

        return messageVO;
    }

    @Override
    public Page<MessageVO> getMessageVOPage(Page<Message> messagePage) {
        List<Message> messageList = messagePage.getRecords();
        Page<MessageVO> messageVOPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
        if (CollectionUtils.isEmpty(messageList)) {
            return messageVOPage;
        }

        // 填充信息
        List<MessageVO> messageVOList = messageList.stream().map(message -> {
            MessageVO messageVO = MessageVO.objToVo(message);

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
        queryWrapper.eq("id_deleted", 0);
        return queryWrapper;
    }

    @Override
    public boolean updateMessage(Message message) {

        // 判断当前用户是否为当前消息的创建人
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long messageUserId = message.getUserId();
        ThrowUtils.throwIf(!loginUserId.equals(messageUserId), ErrorCode.NO_AUTH_ERROR);

        // 更新数据库
        boolean updateResult = this.updateById(message);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);
        return true;
    }
    @Override
    public boolean deleteMessage(Message message) {
        // todo: 考虑加事务

        // 将消息状态设置为已下架
        message.setIsDeleted(1);
        boolean result = this.updateById(message);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return true;
    }
}




