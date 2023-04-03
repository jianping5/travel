package com.travel.user.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author zgy
 * @create 2022-10-15 16:29
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Mail implements Serializable {
    //邮件主题
    private String mailSubject;
    //邮件正文
    private String mailText;
    public static final long serialVersionID = 1L;
}
