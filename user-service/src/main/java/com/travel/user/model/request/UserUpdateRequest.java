package com.travel.user.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 头像 URL
     */
    private String userAvatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 生日
     */
    private Date birth;

    /**
     * 性别（0：女 1：男）
     */
    private Integer sex;
    /**
     * 关注数
     */
    private Integer followCount;
    private static final long serialVersionUID = 1L;
}
