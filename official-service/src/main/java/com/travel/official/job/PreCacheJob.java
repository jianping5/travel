package com.travel.official.job;

import com.travel.common.constant.TypeConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 缓存预热任务
 * @author jianping5
 */
@Component
@Slf4j
public class PreCacheJob {


    @Resource
    private UpdateCache updateCache;

    /**
     * 每天执行，预热推荐周边
     */
    @Scheduled(cron = "0 31 0 * * *")
    public void doCacheRecommendDerivative() {
        updateCache.executeDerivative();
    }

    /**
     * 每天执行，预热推荐官方景区
     */
    @Scheduled(cron = "0 31 0 * * *")
    public void doCacheRecommendOfficial() {
        updateCache.executeOfficial(TypeConstant.OFFICIAL.getTypeIndex());
    }


}
