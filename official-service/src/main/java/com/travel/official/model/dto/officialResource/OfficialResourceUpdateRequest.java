package com.travel.official.model.dto.officialResource;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class OfficialResourceUpdateRequest implements Serializable {

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
     * 封面 URL
     */
    private String coverUrl;

    /**
     * 价格
     */
    private Integer price;

    /**
     * 标题
     */
    private String title;

    /**
     * 类型 id
     */
    private Integer typeId;

    /**
     * 官方资源首句话
     */
    private String intro;

    /**
     * 点赞量
     */
    private Integer likeCount;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 官方资源详情 id
     */
    private Long resourceDetailId;

    /**
     * 资源状态（0：正常 1：异常 2：下架）
     */
    private Integer resourceState;

    /**
     * 官方资源详情
     */
    private String detail;

    private static final long serialVersionUID = 1L;
}
