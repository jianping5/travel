package com.travel.user.model.dto;

import com.travel.user.model.entity.UserInfo;
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
@Document(indexName = "user")
@Data
public class UserEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

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
     * @param userEsDTO
     * @return
     */
    public static UserInfo dtoToObj(UserEsDTO userEsDTO) {
        if (userEsDTO == null) {
            return null;
        }
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userEsDTO, userInfo);
        return userInfo;
    }

    /**
     * 对象转包装类
     *
     * @param userInfo
     * @return
     */
    public static UserEsDTO objToDto(UserInfo userInfo) {
        if (userInfo == null) {
            return null;
        }
        UserEsDTO userEsDTO = new UserEsDTO();
        BeanUtils.copyProperties(userInfo, userEsDTO);

        return userEsDTO;
    }

}
