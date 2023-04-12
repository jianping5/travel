package com.travel.official.model.dto.officialApply;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 3/4/2023 下午 7:00
 */
@Data
public class OfficialApplyAddRequest implements Serializable {

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
     * 类型 id
     */
    private Integer typeId;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 佐证材料
     */
    private String material;

    /**
     * 申请状态（0：待审批 1：已同意 2：已拒绝）
     */
    private Integer applyState;

    private static final long serialVersionUID = 1L;
}
