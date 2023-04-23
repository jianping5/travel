package com.travel.travel.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.google.gson.Gson;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

/**
 * @author jianping5
 * @create 2022/9/29 20:11
 * 刷新token拦截器
 */
@Slf4j
public class RefreshLoginInterceptor implements HandlerInterceptor {

    private RedissonClient redissonClient;

    private Gson gson;

    public RefreshLoginInterceptor(RedissonClient redissonClient, Gson gson) {
        this.redissonClient = redissonClient;
        this.gson = gson;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求头中的 white
        String white = request.getHeader("white");
        log.info("white: " + white);

        // 获取请求头中的 token
        String token = request.getHeader("token");
        log.info("token: " + token);


        // 若为白名单且 token 为 null，说明是未登录也可以访问的接口，直接放行
        if (white != null && token == null) {
            return true;
        }

        if (redissonClient == null) {
            log.info("null redissonClient");
        }

        // 根据 token 获取 user，并设置到 UserHodler 中，便于后续接口调用
        RMap<Object, Object> map = redissonClient.getMap("user_login:" + token);

        // 获取并刷新 user
        User user = BeanUtil.fillBeanWithMap(map, new User(), false);
        map.expire(Duration.ofSeconds(2592000));

        log.info("user: " + user);

        UserHolder.saveUser(user);
        return true;




/*        if (StrUtil.isBlank(token)) {
            // 不进行拦截
            return true;
        }*//*

        // 2.基于token获取redis中的用户
        String tokenKey = "user_login: "  + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        // 3.判断用户是否存在
        if (userMap == null) {
            // 不进行拦截
            return true;
        }
        // 5.将查询返回的Hash数据转为UserDTO对象

        // 6.保存用户信息到ThreadLocal
        UserHolder.saveUser(user);
        // 7.刷新token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.SECONDS);*/
        // return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
