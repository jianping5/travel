package com.travel.user.utils;


import com.travel.user.model.domain.EmailSender;
import com.travel.user.model.domain.Mail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * @author zgy
 * @create 2022-10-15 16:38
 */
@Slf4j
@Component
public class MailUtils {

    private static JavaMailSenderImpl javaMailSender;

    /**
     * 注入邮件发送器
     * @author: ZGY
     * @param emailSend
     * @return void
     */
    @Autowired
    private void setMailSender(EmailSender emailSend) {
        MailUtils.javaMailSender = emailSend.getJavaMailSender();
    }
    /**
     * 检查邮件格式
     * @author: ZGY
     * @param mail
     * @param sendTo
     * @return void
     */
    private static void mailCheck(Mail mail, String sendTo){
        Assert.notNull(sendTo,"收件人不能为空！");
        Assert.notNull(mail.getMailSubject(),"邮件主题不能为空！");
        Assert.notNull(mail.getMailText(),"邮件内容不能为空！");
    }
    /**
     * 发送普通文本邮件
     * @author: ZGY
     * @param mail
     * @param sendTo
     * @return void
     */
    public static boolean sendMail(Mail mail,String sendTo) {
        try {//检查邮件格式
           mailCheck(mail, sendTo);
           //设置邮件
           SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
           //发件人
           simpleMailMessage.setFrom("Co-Help<"+javaMailSender.getUsername()+">");
           //收件人
           simpleMailMessage.setTo(sendTo.split(","));
           //主题
           simpleMailMessage.setSubject(mail.getMailSubject());
           //正文
           simpleMailMessage.setText(mail.getMailText());
           //发送时间
           simpleMailMessage.setSentDate(new Date());
           //发送邮件
           javaMailSender.send(simpleMailMessage);
           log.info("发送邮件成功:{}->{}", javaMailSender.getUsername(), sendTo);
           return true;
       }catch (Exception e){
           log.error("发送邮件失败",e);
           return false;
       }
    }
    /**
     * 发送HTML格式邮件
     * @author: ZGY
     * @param mail
     * @param sendTo
     * @return void
     */
    public static boolean sendHtmlMail(Mail mail, String sendTo)  {
        //设置邮件
        try {
            //检查邮件格式
            mailCheck(mail,sendTo);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            helper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
            //邮件收件人一个或多个
            helper.setTo(sendTo.split(","));
            //邮件主题
            helper.setSubject(mail.getMailSubject());
            //邮件发件人
            helper.setFrom(javaMailSender.getUsername());
            //邮件内容
            helper.setText(mail.getMailText(),true);
            //邮件发送时间
            helper.setSentDate(new Date());
            //发送邮件
            javaMailSender.send(mimeMessage);
            log.info("发送邮件成功:{}->{}",javaMailSender.getUsername(),sendTo);
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败",e);
            return false;
        }
    }
}
