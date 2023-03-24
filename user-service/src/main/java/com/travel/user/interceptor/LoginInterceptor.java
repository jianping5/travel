package com.travel.user.interceptor;

import com.travel.common.common.ErrorCode;
import com.travel.user.model.entity.User;
import com.travel.user.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jianping5
 * @create 2022/9/29 15:44
 * 登录拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        User user = UserHolder.getUser();
        if (user == null) {
            // 不存在 拦截 设置状态码
            response.setStatus(ErrorCode.NO_AUTH_ERROR.getCode());
            return false;
        }
        return true;
    }

}
