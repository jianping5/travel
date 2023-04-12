package com.travel.official.model.dto.official;

import com.travel.official.model.entity.Official;
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
@Document(indexName = "official")
@Data
public class OfficialEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 创建人 id
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
     * 类型 id
     */
    private Integer typeId;

    /**
     * 官方首句话
     */
    private String intro;

    /**
     * 标签
     */
    private String tag;

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
     * @param officialEsDTO
     * @return
     */
    public static Official dtoToObj(OfficialEsDTO officialEsDTO) {
        if (officialEsDTO == null) {
            return null;
        }
        Official official = new Official();
        BeanUtils.copyProperties(officialEsDTO, official);
        return official;
    }

    /**
     * 对象转包装类
     *
     * @param official
     * @return
     */
    public static OfficialEsDTO objToDto(Official official) {
        if (official == null) {
            return null;
        }
        OfficialEsDTO officialEsDTO = new OfficialEsDTO();
        BeanUtils.copyProperties(official, officialEsDTO);

        return officialEsDTO;
    }

}
