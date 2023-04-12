package com.travel.data.service.inner;

import com.travel.common.constant.BehaviorTypeConstant;
import com.travel.common.service.InnerDataService;
import com.travel.data.model.entity.Behavior;
import com.travel.data.service.BehaviorService;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 11/4/2023 下午 7:57
 */
public class InnerDataServiceImpl implements InnerDataService {

    @Resource
    private BehaviorService behaviorService;

    @Override
    public void addBehavior(long loginUserId, int type, long id, BehaviorTypeConstant dislike) {
        // 初始化 behavior
        Behavior behavior = new Behavior();
        behavior.setUserId(loginUserId);
        behavior.setBehaviorObjType(type);
        behavior.setBehaviorObjId(id);
        behavior.setBehaviorType(dislike.getTypeIndex());

        // todo：失败了怎么解决？
        behaviorService.save(behavior);
    }
}
