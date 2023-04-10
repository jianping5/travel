package com.travel.user.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.travel.user.model.entity.Message;
import org.apache.catalina.mbeans.MBeanUtils;

/**
 * @author zgy
 * @create 2023-04-03 22:21
 */
public class MessageVO {

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息关联类型
     */
    private Integer messageObjType;

    /**
     * 消息关联对象 id
     */
    private Long messageObjId;

    /**
     * 消息状态（0：未读 1：已读）
     */
    private Integer messageState;

    public static MessageVO objToVo(Message message){
        if(message == null){
            return null;
        }
        MessageVO messageVO = new MessageVO();
        BeanUtil.copyProperties(message,messageVO);
        return messageVO;
    }
}
