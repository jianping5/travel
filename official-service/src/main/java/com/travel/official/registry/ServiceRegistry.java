package com.travel.official.registry;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.constant.TypeConstant;
import com.travel.official.service.OfficialResourceService;
import com.travel.official.service.OfficialService;
import com.travel.official.service.ReviewService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 构造注册器
 * @author jianping5
 */
@Component
public class ServiceRegistry {

    @Resource
    private OfficialService officialService;

    @Resource
    private OfficialResourceService officialResourceService;

    @Resource
    private ReviewService reviewService;


    private Map<Integer, IService<T>> typeServiceMap;

    @PostConstruct
    public void doInit() {
        System.out.println(1);
        typeServiceMap = new HashMap(20) {{
            put(TypeConstant.OFFICIAL.getTypeIndex(), officialService);
            put(TypeConstant.OFFICIAL_RESOURCE.getTypeIndex(), officialResourceService);
            put(TypeConstant.REVIEW.getTypeIndex(), reviewService);
        }};
    }

    public IService getServiceByType(Integer typeId) {
        if (typeServiceMap == null) {
            return null;
        }
        return typeServiceMap.get(typeId);
    }
}
