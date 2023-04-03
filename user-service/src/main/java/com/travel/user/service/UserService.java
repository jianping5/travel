package com.travel.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.common.BaseResponse;
import com.travel.user.model.entity.User;
import com.travel.user.model.entity.UserInfo;

/**
* @author jianping5
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2023-03-22 14:34:09
*/
public interface UserService extends IService<User> {

   /**
    * 向邮箱发送验证码
    * @param userEmail 用户邮箱
    * @return java.lang.String
    */
   String sendEmailCode(String userEmail);

   /**
    * 邮箱验证码登录
    * @param userEmail 用户邮箱
    * @param code 验证码
    * @return com.travel.common.common.BaseResponse<com.travel.user.model.entity.User>
    */
   BaseResponse<User> loginByEmail(String userEmail, String code);

   /**
    * 根据邮箱/手机号注册账号
    * @param credential 凭证
    * @param credentialType 凭证类型
    * @param password 密码
    * @return com.travel.common.common.BaseResponse<com.travel.user.model.entity.User>
    */
   BaseResponse<User> userRegister(String credential,Integer credentialType,String password);



}
