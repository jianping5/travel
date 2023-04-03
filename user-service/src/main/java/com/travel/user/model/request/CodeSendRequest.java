package com.travel.user.model.request;

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
public class CodeSendRequest implements Serializable {
    /**
     * 凭证
     */
    private String credential;
    /**
     * 凭证类型（0:邮箱,1:手机号）
     */
    private Integer credentialType;
    /**
     * 请求类型（0:注册,1:登录,2:更改密码）
     */
    private Integer codeType;
}
