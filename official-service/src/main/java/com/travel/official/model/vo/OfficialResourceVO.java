package com.travel.official.model.vo;

import com.travel.common.model.dto.UserDTO;
import com.travel.official.model.entity.OfficialResource;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 3/4/2023 下午 4:06
 */
@Data
public class OfficialResourceVO implements Serializable {

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
     * 资源状态（0：正常 1：异常 2：下架）
     */
    private Integer resourceState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创始人信息
     */
    private UserDTO user;

    /**
     * 官方资源详情 id
     */
    private Long resourceDetailId;

    /**
     * 官方资源详情（仅在详情页面传输）
     */
    private String detail;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param officialResourceVO
     * @return
     */
    public static OfficialResource voToObj(OfficialResourceVO officialResourceVO) {
        if (officialResourceVO == null) {
            return null;
        }
        OfficialResource officialResource = new OfficialResource();
        BeanUtils.copyProperties(officialResourceVO, officialResource);
        return officialResource;
    }

    /**
     * 对象转包装类
     *
     * @param officialResource
     * @return
     */
    public static OfficialResourceVO objToVo(OfficialResource officialResource) {
        if (officialResource == null) {
            return null;
        }
        OfficialResourceVO officialResourceVO = new OfficialResourceVO();
        BeanUtils.copyProperties(officialResource, officialResourceVO);

        return officialResourceVO;
    }
}
