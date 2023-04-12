package com.travel.official.model.dto.notification;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
public class NotificationQueryRequest extends PageRequest implements Serializable {

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
     * 搜索词
     */
    private String searchText;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
