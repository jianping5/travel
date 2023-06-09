package com.travel.common.model.dto.official;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 官方表
 * @author jianping5
 * @TableName official
 */
@Data
public class OfficialDTO implements Serializable {
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
     * 点赞量
     */
    private Integer likeCount;

    /**
     * 点评量
     */
    private Integer reviewCount;

    /**
     * 收藏量
     */
    private Integer favoriteCount;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 官方详情 id
     */
    private Long officialDetailId;

    /**
     * 官方详情
     */
    private String detail;

    /**
     * 是否已点赞
     */
    private Integer isLiked;

    private static final long serialVersionUID = 1L;

}