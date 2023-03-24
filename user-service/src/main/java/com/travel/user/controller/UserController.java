package com.travel.user.controller;


import cn.hutool.core.lang.UUID;
import com.google.gson.Gson;
import com.travel.common.service.InnerTeamService;
import com.travel.user.model.entity.User;
import com.travel.user.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author jianping5
 * @createDate 18/3/2023 下午 9:40
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private RedissonClient redissonClient;

    @DubboReference
    private InnerTeamService innerTeamService;

    @Resource
    private Gson gson;

    @GetMapping("/login")
    public String userLogin(HttpServletResponse response) {
        // todo: 校验是否登录，登录成功则获取对应的 user
        User user = new User();

        user.setId(1L);

        // 生成 token
        String token = UUID.fastUUID().toString(true);

        // 以 token 为键，user 的 Json 字符串为值，存入 redis
        RBucket<String> bucket = redissonClient.getBucket("user_login: " + token);
        String userJson = gson.toJson(user, User.class);
        bucket.set(userJson, 2592000, TimeUnit.SECONDS);

        response.setHeader("token", token);
        return token;
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
}
