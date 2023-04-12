package com.travel.official.registry;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.travel.common.constant.TypeConstant;
import com.travel.official.model.entity.Official;
import com.travel.official.model.entity.OfficialResource;
import com.travel.official.model.entity.Review;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 更新构造注册器
 * @author jianping5
 */
@Component
public class UpdateWrapperRegistry {


    private Map<Integer, UpdateWrapper<T>> typeUpdateWrapperMap;

    @PostConstruct
    public void doInit() {
        System.out.println(1);
        typeUpdateWrapperMap = new HashMap(20) {{
            put(TypeConstant.OFFICIAL.getTypeIndex(), new UpdateWrapper<Official>());
            put(TypeConstant.OFFICIAL_RESOURCE.getTypeIndex(), new UpdateWrapper<OfficialResource>());
            put(TypeConstant.REVIEW.getTypeIndex(), new UpdateWrapper<Review>());
        }};
    }

    public UpdateWrapper getUpdateWrapperByType(Integer typeId) {
        if (typeUpdateWrapperMap == null) {
            return null;
        }
        return typeUpdateWrapperMap.get(typeId);
    }
}
