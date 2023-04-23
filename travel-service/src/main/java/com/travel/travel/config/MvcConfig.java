package com.travel.travel.config;

import com.google.gson.Gson;
import com.travel.travel.interceptor.RefreshLoginInterceptor;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @create 2022/9/29 15:54
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private Gson gson;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(new RefreshLoginInterceptor(redissonClient, gson)).order(0);
    }
}
