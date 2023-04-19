package com.travel.data.registry;

import com.travel.common.constant.TypeConstant;
import com.travel.common.service.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 构造注册器
 * @author jianping5
 */
@Component
public class ServiceRegistry {

    @DubboReference
    private InnerOfficialService innerOfficialService;

    @DubboReference
    private InnerTeamService innerTeamService;

    @DubboReference
    private InnerArticleService innerArticleService;

    @DubboReference
    private InnerVideoService innerVideoService;

    private Map<Integer, InnerRcmdService<T>> typeServiceMap;

    @PostConstruct
    public void doInit() {
        typeServiceMap = new HashMap(20) {{
            put(TypeConstant.OFFICIAL.getTypeIndex(), innerOfficialService);
            put(TypeConstant.TEAM.getTypeIndex(), innerTeamService);
            put(TypeConstant.ARTICLE.getTypeIndex(), innerArticleService);
            put(TypeConstant.VIDEO.getTypeIndex(), innerVideoService);
        }};
    }

    public InnerRcmdService getServiceByType(Integer typeId) {
        if (typeServiceMap == null) {
            return null;
        }
        return typeServiceMap.get(typeId);
    }
}
