package com.travel.official.model.dto.official;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 官方查询请求体
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
@ApiModel(value = "官方查询请求体")
public class OfficialQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "官方 id")
    private Long id;

    /**
     * 搜索词
     */
    @ApiModelProperty(value = "搜索词")
    private String searchText;

    /**
     * 所属用户 id
     */
    @ApiModelProperty(value = "所属用户 id")
    private Long userId;

    /**
     * 官方名
     */
    @ApiModelProperty(value = "官方名")
    private String officialName;

    /**
     * 省份
     */
    @ApiModelProperty(value = "省份")
    private String province;

    /**
     * 城市
     */
    @ApiModelProperty(value = "城市")
    private String city;

    /**
     * 地点
     */
    @ApiModelProperty(value = "地点")
    private String location;

    /**
     * 经纬度
     */
    @ApiModelProperty(value = "经纬度")
    private String latAndLong;

    /**
     * 封面 URL
     */
    @ApiModelProperty(value = "封面 URL")
    private String coverUrl;

    /**
     * 视频 URL
     */
    @ApiModelProperty(value = "视频 URL")
    private String videoUrl;

    /**
     * 类型 id（1：景点，2：酒店 3：美食）
     */
    @ApiModelProperty(value = "类型 id（1：景点，2：酒店 3：美食）", required = true)
    private Integer typeId;

    /**
     * 联系方式
     */
    @ApiModelProperty(value = "联系方式")
    private String contact;

    /**
     * 标签（json 数组）
     */
    @ApiModelProperty(value = "标签（json 数组）")
    private String tag;

    /**
     * 官方首句话
     */
    @ApiModelProperty(value = "官方首句话")
    private String intro;

    /**
     * 官方详情 id
     */
    @ApiModelProperty(value = "官方详情 id")
    private Long officialDetailId;

    /**
     * 官方详情
     */
    @ApiModelProperty(value = "官方详情")
    private String detail;

    private static final long serialVersionUID = 1L;
}
