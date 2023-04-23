package com.travel.user.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zgy
 * @create 2023-03-31 14:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "发送验证码请求体")
public class CodeSendRequest implements Serializable {
    @ApiModelProperty(value = "凭证",required = true)
    /**
     * 凭证
     */
    private String credential;
    @ApiModelProperty(value = "凭证类型（0:邮箱,1:手机号）",required = true)
    /**
     * 凭证类型（0:邮箱,1:手机号）
     */
    private Integer credentialType;
    @ApiModelProperty(value = "请求类型（0:注册,1:登录,2:更改密码）",required = true)
    /**
     * 请求类型（0:注册,1:登录,2:更改密码）
     */
    private Integer codeType;
}
