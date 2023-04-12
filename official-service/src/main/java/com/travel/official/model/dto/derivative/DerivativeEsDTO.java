package com.travel.official.model.dto.derivative;

import com.travel.official.model.entity.Derivative;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 28/3/2023 下午 5:36
 */
// todo 取消注释开启 ES（须先配置 ES）
@Document(indexName = "derivative")
@Data
public class DerivativeEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
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
     * 类型 id
     */
    private Integer typeId;

    /**
     * 周边状态（0：正常 1：异常 2：下架）
     */
    private Integer derivativeState;

    /**
     * 创建时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param derivativeEsDTO
     * @return
     */
    public static Derivative dtoToObj(DerivativeEsDTO derivativeEsDTO) {
        if (derivativeEsDTO == null) {
            return null;
        }
        Derivative derivative = new Derivative();
        BeanUtils.copyProperties(derivativeEsDTO, derivative);
        return derivative;
    }

    /**
     * 对象转包装类
     *
     * @param derivative
     * @return
     */
    public static DerivativeEsDTO objToDto(Derivative derivative) {
        if (derivative == null) {
            return null;
        }
        DerivativeEsDTO derivativeEsDTO = new DerivativeEsDTO();
        BeanUtils.copyProperties(derivative, derivativeEsDTO);

        return derivativeEsDTO;
    }

}
