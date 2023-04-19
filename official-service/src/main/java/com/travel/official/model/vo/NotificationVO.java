package com.travel.official.model.vo;

import com.travel.common.model.dto.user.UserDTO;
import com.travel.official.model.entity.Notification;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 6/4/2023 下午 3:50
 */
@Data
public class NotificationVO implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 所属用户 id
     */
    private Long userId;

    /**
     * 所属官方 id
     */
    private Long officialId;

    /**
     * 官方类型 id
     */
    private Integer typeId;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面 URL
     */
    private String coverUrl;

    /**
     * 资讯通知首句话
     */
    private String intro;

    /**
     * 资讯通知详情
     */
    private String detail;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 资源状态（0：正常 1：异常 2：删除）
     */
    private Integer notificationState;

    /**
     * 创始人信息
     */
    private UserDTO user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param notificationVO
     * @return
     */
    public static Notification voToObj(NotificationVO notificationVO) {
        if (notificationVO == null) {
            return null;
        }
        Notification notification = new Notification();
        BeanUtils.copyProperties(notificationVO, notification);
        return notification;
    }

    /**
     * 对象转包装类
     *
     * @param notification
     * @return
     */
    public static NotificationVO objToVo(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationVO notificationVO = new NotificationVO();
        BeanUtils.copyProperties(notification, notificationVO);

        return notificationVO;
    }


}
