package com.travel.user.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgy
 * @create 2023-04-02 0:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "校验验证码请求体")
public class CodeCheckRequest {
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
    @ApiModelProperty(value = "验证码类型",required = true)
    /**
     * 验证码类型
     */
    private Integer codeType;
    @ApiModelProperty(value = "验证码",required = true)
    /**
     * 验证码
     */
    private String code;

}
