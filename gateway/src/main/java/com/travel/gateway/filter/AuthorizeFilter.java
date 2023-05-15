package com.travel.gateway.filter;

import cn.hutool.core.bean.BeanUtil;
import com.google.gson.Gson;
import com.travel.gateway.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 * 认证过滤器
 * @author jianping5
 */
@Slf4j
@Order(-1)
@Component
public class AuthorizeFilter implements GlobalFilter {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private Gson gson;

    /**
     * 白名单
     */
    private final List<String> white_path_list = Arrays.asList("/login", "/login/test","/code/send","/code/check","register", "/v2/api-docs",
            "/doc.html", "/swagger-resources", "/swagger-ui.html", "/swagger-resources/configuration/ui",
            "/swagger-resources/configuration/security", "/v2/api-docs-ext");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        RequestPath path = request.getPath();

        // 白名单放行
        if (white_path_list.contains(path.value())) {
            log.info("white path: " + path.value());

            // 设置 white 到 header 中（服务由 white 和 token 综合判断是否需要放行）
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("white", "white");
            }).build();
            exchange.mutate().request(serverHttpRequest).build();
            // 白名单放行
            return chain.filter(exchange);
        }

        log.info("path: " + path.value());

        // 获取请求头（authentication）
        HttpHeaders headers = request.getHeaders();
        List<String> authentication = headers.get("authentication");

        // 获取 token
        String token = authentication.get(0);
        if (token == null) {
            handleNoAuth(response);
        }
        log.info("token: " + token);

        // 用 token 去 redis 中查询对应的用户
        // 根据 token 获取 user，并设置到 UserHodler 中，便于后续接口调用
        RMap<Object, Object> map = redissonClient.getMap("user_login:" + token);

        // 获取并刷新 user
        User user = BeanUtil.fillBeanWithMap(map, new User(), false);
        // map.expire(Duration.ofSeconds(2592000));

        // RBucket<String> bucket = redissonClient.getBucket("user_login:" + token);
        // User user = gson.fromJson(bucket.get(), User.class);

        // 若用户存在则放行
        if (user != null) {
            // 设置 token 到 header 中
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("token", token);
            }).build();

            log.info("userId: " + user.getId());

            exchange.mutate().request(serverHttpRequest).build();
            return chain.filter(exchange);
        }

        // 否则返回未授权
        return handleNoAuth(response);
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }
}
