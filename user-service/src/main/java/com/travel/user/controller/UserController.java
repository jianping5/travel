package com.travel.user.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.service.InnerTeamService;
import com.travel.common.utils.UserHolder;
import com.travel.user.constant.CodeType;
import com.travel.user.constant.CredentialType;
import com.travel.user.model.dto.UserVO;

import com.travel.user.model.entity.User;
import com.travel.user.model.entity.UserInfo;
import com.travel.user.model.request.*;
import com.travel.user.service.UserInfoService;
import com.travel.user.service.UserService;
import com.travel.user.utils.FormatValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
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
@Api(tags = "用户 Controller")
@RestController
public class UserController {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;

    @Resource
    private UserInfoService userInfoService;

    @DubboReference
    private InnerTeamService innerTeamService;

    @Resource
    private Gson gson;

    @PostMapping("/code/send")
    @ApiOperation(value = "发送验证码")
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
    @ApiOperation(value = "校验验证码")
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
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR,"验证码不存在，请重新发送验证码");
        }else {
            if(redisCode.equals(codeCheckRequest.getCode())){
                bucket.expire(Duration.ZERO);
                //注册用途：生成注册码并存入redis
                if(CodeType.equals(codeType,CodeType.REGISTER)){
                    String registerCode = UUID.fastUUID().toString(true);
                    RSetCache<String> setCache = redissonClient.getSetCache("travel:user:register-code");
                    setCache.add(registerCode,900,TimeUnit.SECONDS);
                    return ResultUtils.success(registerCode);
                }
                //todo：其余用途的验证码校验
                return ResultUtils.success(null);
            }else {
                return ResultUtils.error(null,"验证码错误");
            }
        }
    }
    @ApiOperation(value = "注册")
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
    @ApiOperation(value = "登录")
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
        RMap<String, Object> map = redissonClient.getMap("user_login:" + token);
        map.putAll(userMap);
        map.expire(Duration.ofSeconds(2592000));

        // 给响应头设置token
        response.setHeader("token", token);
        return ResultUtils.success(user);
    }
    @ApiOperation(value = "登录测试")
    @PostMapping("/login/test")
    public BaseResponse<User> userLoginTest(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        User user = null;
        // 获取账号和密码
        String userAccount = loginRequest.getCredential();
        String userPassword = loginRequest.getPasscode();

        // 登录
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("user_account", userAccount);
        UserInfo one = userInfoService.getOne(userInfoQueryWrapper);
        if (one != null && one.getUserPassword().equals(userPassword)) {
            user = userService.getById(one.getUserId());
            user = userService.traverseUser(user);
        } else {
            // 注册
            //插入 user 表
            User user0 = new User();
            user0.setUserRole(0);
            boolean userSave = userService.save(user0);
            user = userService.getById(user0.getId());

            // 插入 user_info 表
            UserInfo userInfo0 = new UserInfo();
            userInfo0.setUserId(user.getId());
            userInfo0.setUserAccount(userAccount);
            userInfo0.setUserPassword(userPassword);
            userInfo0.setUserName("user_" + RandomUtils.nextInt());
            userInfo0.setUserAvatar("https://jp-typora-1310703557.cos.ap-shanghai.myqcloud.com/2023/01/13/a3fae8d453be485f9ecc04235ed753a9.PNG");

            boolean save = userInfoService.save(userInfo0);
            UserInfo userInfo = userInfoService.getById(userInfo0.getId());
            user.setUserInfo(userInfo);
            user = userService.traverseUser(user);
        }

        // 生成 token
        String token = UUID.fastUUID().toString(true);

        // 以 token 为键，将过滤后的user存入 redis
        Map<String, Object> userMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreProperties("createTime", "updateTime", "userInfo")
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        RMap<String, Object> map = redissonClient.getMap("user_login:" + token);
        map.putAll(userMap);
        map.expire(Duration.ofSeconds(2592000));

        // 给响应头设置token
        response.setHeader("token", token);
        return ResultUtils.success(user);
    }


    @ApiOperation(value = "退出登录")
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        com.travel.common.model.entity.User user = UserHolder.getUser();
        log.info(user.toString());

        String token = request.getHeader("token");
        log.info("token logout: " + token);

        // 删除当前用户的 token
        RBucket<String> bucket = redissonClient.getBucket("user_login:" + token);
        if (bucket != null) {
            bucket.delete();
        }
        return "logout";
    }

    @ApiOperation(value = "根据id查询用户")
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(Long id) {
        // 如果不传用户 id，则默认为当前用户 id
        if (id == null) {
            com.travel.common.model.entity.User user = UserHolder.getUser();
            id = user.getId();
        }


        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        UserInfo userInfo = userInfoService.getOne(new QueryWrapper<UserInfo>().eq("user_id", user.getId()));
        userInfo.setUserPassword(null);
        user.setUserInfo(userInfo);
        return ResultUtils.success(userService.getUserVO(user));
    }

}
