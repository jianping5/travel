package com.travel.user.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zgy
 * @create 2023-03-31 15:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "登录请求体")
public class LoginRequest implements Serializable {

    @ApiModelProperty(required = true,value = "凭证(账号，邮箱，手机号)")
    /**
     * 凭证
     */
    private String credential;

    @ApiModelProperty(required = true,value = "口令")
    /**
     * 口令
     */
    private String passcode;

    @ApiModelProperty(required = true,value = "凭证类型")
    /**
     * 凭证类型
     */
    private Integer credentialType;
}
