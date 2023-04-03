package com.travel.user.model.request;

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
public class LoginRequest implements Serializable {

    /**
     * 凭证
     */
    private String credential;
    /**
     * 口令
     */
    private String passcode;
    /**
     * 凭证类型
     */
    private Integer credentialType;
}
