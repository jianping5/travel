package com.travel.user.model.request;

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
public class CodeCheckRequest {
    /**
     * 凭证
     */
    private String credential;
    /**
     * 凭证类型（0:邮箱,1:手机号）
     */
    private Integer credentialType;
    /**
     * 验证码类型
     */
    private Integer codeType;
    /**
     * 验证码
     */
    private String code;

}
