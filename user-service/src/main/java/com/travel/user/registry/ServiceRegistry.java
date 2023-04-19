package com.travel.user.registry;


import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.constant.TypeConstant;
import com.travel.common.service.ArticleService;
import com.travel.common.service.CommentService;
import com.travel.common.service.VideoService;
import com.travel.user.service.UserInfoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zgy
 * @create 2023-04-17 17:54
 */
@Component
public class ServiceRegistry {
    @Resource
    private UserInfoService userInfoService;

    @DubboReference
    private ArticleService articleService;

    @DubboReference
    private VideoService videoService;

    @DubboReference
    private CommentService commentService;

    private Map<Integer, IService<T>> typeServiceMap;

    @PostConstruct
    public void doInit() {
        typeServiceMap = new HashMap(20) {{
            put(TypeConstant.USER.getTypeIndex(), userInfoService);
            put(TypeConstant.ARTICLE.getTypeIndex(), articleService);
            put(TypeConstant.VIDEO.getTypeIndex(), videoService);
            put(TypeConstant.COMMENT.getTypeIndex(),commentService);
        }};
    }

    public IService getServiceByType(Integer typeId) {
        if (typeServiceMap == null) {
            return null;
        }
        return typeServiceMap.get(typeId);
    }
}
