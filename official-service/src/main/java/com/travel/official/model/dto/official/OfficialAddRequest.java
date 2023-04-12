package com.travel.official.model.dto.official;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class OfficialAddRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 所属用户 id
     */
    private Long userId;

    /**
     * 官方名
     */
    private String officialName;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 地点
     */
    private String location;

    /**
     * 经纬度
     */
    private String latAndLong;

    /**
     * 封面 URL
     */
    private String coverUrl;

    /**
     * 视频 URL
     */
    private String videoUrl;

    /**
     * 类型 id
     */
    private Integer typeId;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 标签
     */
    private String tag;

    /**
     * 官方首句话
     */
    private String intro;

    /**
     * 官方详情 id
     */
    private Long officialDetailId;

    /**
     * 官方详情
     */
    private String detail;

    private static final long serialVersionUID = 1L;

}
