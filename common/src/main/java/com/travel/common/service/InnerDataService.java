package com.travel.common.service;

import com.travel.common.constant.BehaviorTypeConstant;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 8:57
 */
public interface InnerDataService {
    /**
     * 添加行为记录
     * @param loginUserId
     * @param type
     * @param id
     * @param dislike
     */
    void addBehavior(long loginUserId, int type, long id, BehaviorTypeConstant dislike);
    void addBehavior(long loginUserId, int type, long id, Integer behaviorType);
}
