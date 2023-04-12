package com.travel.official.model.dto.notification;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class NotificationUpdateRequest implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
     * 资源状态（0：正常 1：异常 2：删除）
     */
    private Integer notificationState;

    private static final long serialVersionUID = 1L;
}
