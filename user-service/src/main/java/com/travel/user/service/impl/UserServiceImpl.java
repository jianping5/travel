package com.travel.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.user.constant.CodeType;
import com.travel.user.constant.CredentialType;
import com.travel.user.model.domain.Mail;
import com.travel.user.model.dto.UserVO;
import com.travel.user.model.entity.User;
import com.travel.user.model.entity.UserInfo;
import com.travel.user.service.UserInfoService;
import com.travel.user.service.UserService;
import com.travel.user.mapper.UserMapper;
import com.travel.user.utils.MailUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RBucket;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;

/**
* @author jianping5
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
@DubboService
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserInfoService userInfoService;

    @Override
    public String sendEmailCode(String userEmail) {
        //检查参数是否有效
        if(null==userEmail||userEmail.equals("")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //随机生成六位数字验证码
        Random random = new Random();
        Integer confirmCode = random.nextInt(899999)+100000;
        //发送验证码
        String message = "您的验证码是："+confirmCode.toString()+"。5分钟内有效，请妥善保管验证码，谨防泄露，如非本人操作请忽略！";
        boolean status = MailUtils.sendMail(new Mail("Co-Help验证码", message), userEmail);
        if (status) {
            return confirmCode.toString();
        }
        else {
            return null;
        }
    }

    @Override
    public BaseResponse<User> loginByEmail(String userEmail, String code) {
        //校验参数
        if(ObjectUtils.anyNull(userEmail,code)|| StringUtils.isAnyBlank(userEmail,code)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //核对验证码
        RBucket<String> bucket = redissonClient.getBucket("travel:user:code:"+
                CodeType.LOGIN.ordinal()+"-"+ CredentialType.EMAIL.ordinal() +"-"+userEmail);
        String passcode = bucket.get();
        if(passcode!=null){
            if(passcode.equals(code)){
                UserInfo userInfo = userInfoService.getOne(new QueryWrapper<UserInfo>().eq("email", userEmail));
                if(userInfo != null){
                    User user = getOne(new QueryWrapper<User>().eq("id", userInfo.getUserId()));
                    if(user!=null){
                        user.setUserInfo(userInfo);
                        User baseUser = traverseUser(user);
                        bucket.expire(Duration.ZERO);
                        return new BaseResponse(baseUser,"登录成功");
                    }else {
                        return new BaseResponse(null,"查询不到用户官方信息");
                    }
                }else {
                    return new BaseResponse(null,"查询不到匹配用户");
                }
            }else {
                return new BaseResponse(null,"验证码错误");
            }
        }else {
            return new BaseResponse(null,"验证码不存在，请重新发送验证码");
        }
    }

    @Override
    //todo: 事务控制，userInfo插入必须和user插入绑定成原子操作
    public BaseResponse<User> userRegister(String credential, Integer credentialType, String password) {

        //参数校验
        if(ObjectUtils.anyNull(credential,credentialType,password)
                ||StringUtils.isAnyBlank(credential,password)
                ||!CredentialType.isValid(credentialType)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //校验邮箱手机号绑定状态
        if(CredentialType.isEmail(credentialType)){
            //校验邮箱是否已绑定
            UserInfo userInfoByEmail = userInfoService.getOne(new QueryWrapper<UserInfo>().eq("email", credential));
            if(userInfoByEmail!=null){
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"注册失败，邮箱已绑定");
            }
        }else if(CredentialType.isPhoneNumber(credentialType)){
            //校验手机号是否已绑定
            UserInfo userInfoByEmail = userInfoService.getOne(new QueryWrapper<UserInfo>().eq("phone", credential));
            if(userInfoByEmail!=null){
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"注册失败，手机号已绑定");
            }
        }

        //插入数据库
        User user0 = new User();
        user0.setUserRole(0);
        boolean userSave = save(user0);
        User user = getById(user0.getId());
        if(userSave){
            UserInfo userInfo0 = new UserInfo();
            userInfo0.setUserId(user.getId());
            userInfo0.setUserAccount("user"+user.getId());
            userInfo0.setUserName("默认昵称");
            userInfo0.setUserAvatar("https://jp-typora-1310703557.cos.ap-shanghai.myqcloud.com/2023/01/13/a3fae8d453be485f9ecc04235ed753a9.PNG");
            // todo:加密密码
            userInfo0.setUserPassword(password);
            if(CredentialType.isEmail(credentialType)){
                userInfo0.setEmail(credential);
            }else if(CredentialType.isPhoneNumber(credentialType)){
                userInfo0.setPhone(credential);
            }
            boolean save = userInfoService.save(userInfo0);
            if(save){
                UserInfo userInfo = userInfoService.getById(userInfo0.getId());
                user.setUserInfo(userInfo);
                User baseUser = traverseUser(user);
                return ResultUtils.success(baseUser);
            }else {
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"注册失败，请稍后尝试");
            }
        }else {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"注册失败，请稍后尝试");
        }
    }

    @Override
    public UserVO getUserVO(User user) {
        if(user==null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user,userVO);
        BeanUtil.copyProperties(user.getUserInfo(),userVO);
        return userVO;
    }


    @Override
    public User traverseUser(User user){
        if(user==null){
            return null;
        }
        User baseUser = new User();
        BeanUtil.copyProperties(user,baseUser);
        if(baseUser.getUserInfo()!=null) {
            baseUser.getUserInfo().setUserPassword(null);
            baseUser.getUserInfo().setCreateTime(null);
            baseUser.getUserInfo().setUpdateTime(null);
        }
        baseUser.setCreateTime(null);
        baseUser.setUpdateTime(null);
        return baseUser;
    }
}




