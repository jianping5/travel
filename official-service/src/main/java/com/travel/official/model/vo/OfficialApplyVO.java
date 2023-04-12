package com.travel.official.model.vo;

import com.travel.common.model.dto.UserDTO;
import com.travel.official.model.entity.Official;
import com.travel.official.model.entity.OfficialApply;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 官方申请视图体
 * @author jianping5
 * @TableName official_apply
 */
@Data
public class OfficialApplyVO implements Serializable {
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 申请人信息
     */
    private UserDTO user;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param officialApplyVO
     * @return
     */
    public static OfficialApply voToObj(OfficialApplyVO officialApplyVO) {
        if (officialApplyVO == null) {
            return null;
        }
        OfficialApply officialApply = new OfficialApply();
        BeanUtils.copyProperties(officialApplyVO, officialApply);
        return officialApply;
    }

    /**
     * 对象转包装类
     *
     * @param officialApply
     * @return
     */
    public static OfficialApplyVO objToVo(OfficialApply officialApply) {
        if (officialApply == null) {
            return null;
        }
        OfficialApplyVO officialApplyVO = new OfficialApplyVO();
        BeanUtils.copyProperties(officialApply, officialApplyVO);

        return officialApplyVO;
    }
}