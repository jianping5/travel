package com.travel.official.model.dto.notification;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 资讯通知查询请求体
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
@ApiModel(value = "资讯通知查询请求体")
public class NotificationQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "资讯通知 id")
    private Long id;

    /**
     * 所属用户 id
     */
    @ApiModelProperty(value = "所属用户 id")
    private Long userId;

    /**
     * 所属官方 id
     */
    @ApiModelProperty(value = "所属官方 id")
    private Long officialId;

    /**
     * 搜索词
     */
    @ApiModelProperty(value = "搜索词")
    private String searchText;

    /**
     * 官方类型 id
     */
    @ApiModelProperty(value = "官方类型 id")
    private Integer typeId;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 资讯通知首句话
     */
    @ApiModelProperty(value = "资讯通知首句话")
    private String intro;

    /**
     * 资讯通知详情
     */
    @ApiModelProperty(value = "资讯通知详情")
    private String detail;

    /**
     * 浏览量
     */
    @ApiModelProperty(value = "浏览量")
    private Integer viewCount;

    /**
     * 资源状态（0：正常 1：异常 2：删除）
     */
    @ApiModelProperty(value = "资源状态（0：正常 1：异常 2：删除）")
    private Integer notificationState;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
