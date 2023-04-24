package com.travel.official.model.dto.notification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 资讯通知添加请求体
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "资讯通知添加请求体")
public class NotificationAddRequest implements Serializable {

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
     * 官方类型 id
     */
    @ApiModelProperty(value = "官方类型 id")
    private Integer typeId;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题", required = true)
    private String title;

    /**
     * 封面 URL
     */
    @ApiModelProperty(value = "封面 URL", required = true)
    private String coverUrl;

    /**
     * 资讯通知首句话
     */
    @ApiModelProperty(value = "资讯通知首句话", required = true)
    private String intro;

    /**
     * 资讯通知详情
     */
    @ApiModelProperty(value = "资讯通知详情", required = true)
    private String detail;

    /**
     * 资源状态（0：正常 1：异常 2：删除）
     */
    @ApiModelProperty(value = "资源状态（0：正常 1：异常 2：删除）")
    private Integer notificationState;


    private static final long serialVersionUID = 1L;

}
