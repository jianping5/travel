package com.travel.common.model.vo;

import com.travel.common.model.dto.official.DerivativeDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 31/3/2023 下午 4:17
 */
@Data
public class DerivativeVDTO implements Serializable {

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
     * 周边名称
     */
    private String derivativeName;

    /**
     * 封面 URL
     */
    private String coverUrl;

    /**
     * 周边价格
     */
    private Double price;

    /**
     * 周边介绍
     */
    private String intro;

    /**
     * 获取方式（0：现金 1：代币）
     */
    private Integer obtainMethod;

    /**
     * 周边数量
     */
    private Integer totalCount;

    /**
     * 类型 id
     */
    private Integer typeId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 周边获取次数
     */
    private Integer obtainCount;

    /**
     * 周边状态（0：正常 1：异常 2：下架）
     */
    private Integer derivativeState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param derivativeVDTO
     * @return
     */
    public static DerivativeDTO voToObj(DerivativeVDTO derivativeVDTO) {
        if (derivativeVDTO == null) {
            return null;
        }
        DerivativeDTO derivativeDTO = new DerivativeDTO();
        BeanUtils.copyProperties(derivativeVDTO, derivativeDTO);
        return derivativeDTO;
    }

    /**
     * 对象转包装类
     *
     * @param derivativeDTO
     * @return
     */
    public static DerivativeVDTO objToVo(DerivativeDTO derivativeDTO) {
        if (derivativeDTO == null) {
            return null;
        }
        DerivativeVDTO derivativeVDTO = new DerivativeVDTO();
        BeanUtils.copyProperties(derivativeDTO, derivativeVDTO);

        return derivativeVDTO;
    }
}
