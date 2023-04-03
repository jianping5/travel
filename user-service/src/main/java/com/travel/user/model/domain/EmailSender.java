package com.travel.user.model.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

/**
 * @author zgy
 * @create 2022-10-16 12:35
 */
@Data
@Component
@Import(JavaMailSenderImpl.class)
@ConfigurationProperties(prefix = "spring.mail")
public class EmailSender {
    //发送方邮箱
    private String username;
    //授权码
    private String password;
    //服务器地址
    private String host;
    //端口号
    private int port;
    @Autowired
    private JavaMailSenderImpl javaMailSender;
    /**
     * 配置邮件发送器
     * @author: ZGY
     * @param
     * @return void
     */
    private void setConfig(){
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
    }
    public JavaMailSenderImpl getJavaMailSender() {
        setConfig();
        return javaMailSender;
    }
}
