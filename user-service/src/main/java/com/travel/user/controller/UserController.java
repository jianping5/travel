package com.travel.user.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.service.InnerTeamService;
import com.travel.user.constant.CodeType;
import com.travel.user.constant.CredentialType;
import com.travel.user.model.dto.UserVO;
import com.travel.user.model.entity.User;
import com.travel.user.model.entity.UserInfo;
import com.travel.user.model.request.*;
import com.travel.user.service.UserService;
import com.travel.user.utils.FormatValidator;
import com.travel.user.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.search.aggregations.pipeline.Derivative;
import org.redisson.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jianping5
 * @createDate 18/3/2023 下午 9:40
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource()
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;

    @DubboReference
    private InnerTeamService innerTeamService;

    @Resource
    private Gson gson;

    @PostMapping("/code/send")
    public BaseResponse<Object> codeSend(@RequestBody CodeSendRequest codeSendRequest){

        //参数判空
        if(codeSendRequest == null|| ObjectUtils.anyNull(codeSendRequest.getCredential(), codeSendRequest.getCodeType(), codeSendRequest.getCredentialType())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //检验凭证格式
        String credential = codeSendRequest.getCredential();
        if(!FormatValidator.validateEmail(credential)&&!FormatValidator.validatePhone(credential)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"凭证格式不合法");
        }

        //发送验证码
        String code = null;
        if(CredentialType.isEmail(codeSendRequest.getCredentialType())){
            //发送邮箱验证码
            code = userService.sendEmailCode(codeSendRequest.getCredential());
        }else if(CredentialType.isPhoneNumber(codeSendRequest.getCredentialType())){
            // todo : 手机验证码发送
        }

        //将验证码存入redis
        if(code==null||code.equals("")){
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }else {
            RBucket<String> bucket = null;
            Integer codeType = codeSendRequest.getCodeType();
            Integer credentialType = codeSendRequest.getCredentialType();
            //redis验证码key格式：travel:user:code:验证码类型-凭证类型-凭证
            if(CodeType.isCodeType(codeType)){
                if(CodeType.equals(codeType,CodeType.REGISTER)){
                    bucket = redissonClient.getBucket("travel:user:code:"+codeType+"-"+
                            credentialType+"-"+ codeSendRequest.getCredential());
                }else if(CodeType.equals(codeType,CodeType.LOGIN)){
                    bucket = redissonClient.getBucket("travel:user:code:"+codeType+"-"+
                            credentialType+"-"+ codeSendRequest.getCredential());
                }else if(CodeType.equals(codeType,CodeType.CHANGE_PASSWORD)){
                    bucket = redissonClient.getBucket("travel:user:code:"+codeType+"-"+
                            credentialType+"-"+ codeSendRequest.getCredential());
                }
                bucket.set(code);
                bucket.expire(Duration.ofSeconds(300));
                //返回操作结果
                return ResultUtils.success(null);
            }else {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求格式不合法");
            }
        }
    }

    @PostMapping("/code/check")
    public BaseResponse<String> codeCheck(@RequestBody CodeCheckRequest codeCheckRequest){

        //参数判空
        if(codeCheckRequest == null||ObjectUtils.anyNull(codeCheckRequest.getCredential(), codeCheckRequest.getCode(), codeCheckRequest.getCredentialType())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //从redis获取验证码
        String credential = codeCheckRequest.getCredential();
        Integer credentialType = codeCheckRequest.getCredentialType();
        Integer codeType = codeCheckRequest.getCodeType();
        RBucket<String> bucket = redissonClient.getBucket("travel:user:code:" + codeType + "-" + credentialType + "-" + credential);
        String redisCode = bucket.get();

        //校验验证码
        if(redisCode==null){
            return ResultUtils.error(null,"验证码不存在，请重新发送验证码");
        }else {
            if(redisCode.equals(codeCheckRequest.getCode())){
                bucket.expire(Duration.ZERO);
                //注册用途：生成注册码并存入redis
                if(CodeType.equals(codeType,CodeType.REGISTER)){
                    String registerCode = UUID.fastUUID().toString(true);
                    RSetCache<String> setCache = redissonClient.getSetCache("travel:user:register-code");
                    setCache.add(registerCode,900,TimeUnit.SECONDS);
                    return ResultUtils.success(registerCode,"验证通过");
                }
                //todo：其余用途的验证码校验
                return ResultUtils.success(null,"验证通过");
            }else {
                return ResultUtils.error(null,"验证码错误");
            }
        }
    }

    @PostMapping("/register")
    public BaseResponse<User> userRegister(@RequestBody RegisterRequest registerRequest){

        //参数判空
        if(registerRequest==null||ObjectUtils.anyNull(registerRequest.getCredential(),
                registerRequest.getRegisterCode(),registerRequest.getCredentialType())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //检验两次密码是否一致
        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())){
            ResultUtils.error(ErrorCode.PARAMS_ERROR,"两次输入密码不一致");
        }

        //从redis获取注册码
        String credential = registerRequest.getCredential();
        Integer credentialType = registerRequest.getCredentialType();
        String registerCode = registerRequest.getRegisterCode();
        String password = registerRequest.getPassword();
        RSetCache<String> setCache = redissonClient.getSetCache("travel:user:register-code");
        boolean contains = setCache.contains(registerCode);
        if(contains){
            setCache.remove(registerCode);
            return userService.userRegister(credential, credentialType, password);
        }else {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR,"注册码不存在");
        }
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        //参数判空
        if(loginRequest == null||!CredentialType.isValid(loginRequest.getCredentialType())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //根据登录类型执行登录业务
        BaseResponse<User> baseResponse = null;
        Integer type = loginRequest.getCredentialType();
        if(CredentialType.isAccount(type)){
            // todo:待完成账号密码登录
        }else if(CredentialType.isEmail(type)){
            baseResponse = userService.loginByEmail(loginRequest.getCredential(), loginRequest.getPasscode());
        }else if(CredentialType.isPhoneNumber(type)){
            // todo:待完成手机验证码登录
        }else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取user
        User user = baseResponse.getData();
        if(user==null){
            return ResultUtils.error(ErrorCode.FORBIDDEN_ERROR,baseResponse.getMessage());
        }

        // 生成 token
        String token = UUID.fastUUID().toString(true);

        // 以 token 为键，将过滤后的user存入 redis
        Map<String, Object> userMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreProperties("createTime", "updateTime", "userInfo")
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        RMap<String, Object> map = redissonClient.getMap("user_login: " + token);
        map.putAll(userMap);
        map.expire(Duration.ofSeconds(2592000));

        // 给响应头设置token
        response.setHeader("token", token);
        return ResultUtils.success(user);
    }

    @PostMapping("/login/test")
    public BaseResponse<User> userLoginTest(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        // 获取user
        User user = userService.getById(1L);

        // 生成 token
        String token = UUID.fastUUID().toString(true);

        // 以 token 为键，将过滤后的user存入 redis
        Map<String, Object> userMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreProperties("createTime", "updateTime", "userInfo")
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        RMap<String, Object> map = redissonClient.getMap("user_login: " + token);
        map.putAll(userMap);
        map.expire(Duration.ofSeconds(2592000));

        // 给响应头设置token
        response.setHeader("token", token);
        return ResultUtils.success(user);
    }

    @PostMapping("/user/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        // 校验团队更新请求体
        if (userUpdateRequest == null || userUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将团队更新请求体的内容赋值给 团队
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userUpdateRequest, userInfo);

        // 参数校验
        userService.validUser(userInfo, false);
        long id = userUpdateRequest.getId();

        // 判断是否存在
        User oldPost = userService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新用户信息
        userService.updateUser(userInfo);

        return ResultUtils.success(true);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        User user = UserHolder.getUser();
        log.info(user.toString());

        String token = request.getHeader("token");
        log.info("token logout: " + token);

        // 删除当前用户的 token
        RBucket<String> bucket = redissonClient.getBucket("user_login: " + token);
        if (bucket != null) {
            bucket.delete();
        }
        return "logout";
    }


    /**
     * 根据 id 获取用户视图图详情
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userService.getUserVO(user));
    }

}
