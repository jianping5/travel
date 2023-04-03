package com.travel.user.model.request;

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
public class RegisterRequest {
    /**
     * 凭证
     */
    private String credential;
    /**
     * 凭证类型
     */
    private Integer credentialType;
    /**
     * 密码
     */
    private String password;
    /**
     * 确认密码
     */
    private String confirmPassword;
    /**
     * 注册码
     */
    private String registerCode;
}
