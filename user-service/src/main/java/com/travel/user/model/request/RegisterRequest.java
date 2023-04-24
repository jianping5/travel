package com.travel.user.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgy
 * @create 2023-04-02 12:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "注册请求体")
public class RegisterRequest {

    @ApiModelProperty(required = true,value = "凭证")
    /**
     * 凭证
     */
    private String credential;
    @ApiModelProperty(required = true,value = "凭证类型")
    /**
     * 凭证类型
     */
    private Integer credentialType;
    @ApiModelProperty(required = true,value = "密码")
    /**
     * 密码
     */
    private String password;
    @ApiModelProperty(required = true,value = "确认密码")
    /**
     * 确认密码
     */
    private String confirmPassword;
    @ApiModelProperty(required = true,value = "注册码")
    /**
     * 注册码
     */
    private String registerCode;
}
