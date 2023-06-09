package com.travel.common.model.vo;

import com.travel.common.model.dto.official.OfficialDTO;
import com.travel.common.model.dto.user.UserDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 4/4/2023 下午 6:17
 */
@Data
public class OfficialVDTO implements Serializable {
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
     * 创始人信息
     */
    private UserDTO user;

    /**
     * 官方详情 id
     */
    private Long officialDetailId;

    /**
     * 官方详情（仅在详情页面传输）
     */
    private String detail;

    /**
     * 是否已点赞
     */
    private Integer isLiked;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param officialVDTO
     * @return
     */
    public static OfficialDTO voToObj(OfficialVDTO officialVDTO) {
        if (officialVDTO == null) {
            return null;
        }
        OfficialDTO officialDTO = new OfficialDTO();
        BeanUtils.copyProperties(officialVDTO, officialDTO);
        return officialDTO;
    }

    /**
     * 对象转包装类
     *
     * @param officialDTO
     * @return
     */
    public static OfficialVDTO objToVo(OfficialDTO officialDTO) {
        if (officialDTO == null) {
            return null;
        }
        OfficialVDTO officialVDTO = new OfficialVDTO();
        BeanUtils.copyProperties(officialDTO, officialVDTO);

        return officialVDTO;
    }

}
