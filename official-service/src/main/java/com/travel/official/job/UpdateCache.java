package com.travel.official.job;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.gson.Gson;
import com.travel.common.constant.BehaviorTypeConstant;
import com.travel.common.service.InnerDataService;
import com.travel.common.service.InnerUserService;
import com.travel.official.model.entity.Derivative;
import com.travel.official.model.entity.Official;
import com.travel.official.model.vo.DerivativeVO;
import com.travel.official.model.vo.OfficialVO;
import com.travel.official.registry.ServiceRegistry;
import com.travel.official.registry.UpdateWrapperRegistry;
import com.travel.official.service.DerivativeService;
import com.travel.official.service.OfficialResourceService;
import com.travel.official.service.OfficialService;
import com.travel.official.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author jianping5
 * @createDate 30/3/2023 下午 5:58
 */
@Component
@Slf4j
public class UpdateCache {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private DerivativeService derivativeService;

    @Resource
    private OfficialService officialService;

    @Resource
    private OfficialResourceService officialResourceService;

    @Resource
    private InnerDataService innerDataService;

    @Resource
    private ReviewService reviewService;

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private UpdateWrapperRegistry updateWrapperRegistry;

    @Resource
    private ServiceRegistry serviceRegistry;

    @Resource
    private Gson gson;

    /**
     * 更新周边缓存
     */
    public void executeDerivative() {
        RLock lock = redissonClient.getLock("travel:precache:derivative:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());

                // 缓存 key
                String redisKey = "travel:derivative:recommend";

                // 判断缓存是否存在，若存在则直接退出
                RList<Object> oldList = redissonClient.getList(redisKey);
                if (CollectionUtils.isNotEmpty(oldList)) {
                    return;
                }

                // 查询推荐的团队列表（分页查询）
                QueryWrapper<Derivative> derivativeQueryWrapper = new QueryWrapper<>();
                derivativeQueryWrapper.last("order by 5*obtain_count+3*view_count desc limit 50");
                List<Derivative> derivativeList = derivativeService.list(derivativeQueryWrapper);
                List<DerivativeVO> derivativeVOList = derivativeList.stream().map(derivative -> derivativeService.getDerivativeVO(derivative)).collect(Collectors.toList());

                // 写缓存
                try {
                    // todo: 需要转换成 json 格式再添加吗
                    RList<String> list = redissonClient.getList(redisKey);
                    List<String> derivativeVOStrList = derivativeVOList.stream().map(derivativeVO -> gson.toJson(derivativeVO)).collect(Collectors.toList());
                    list.addAll(derivativeVOStrList);
                    list.expire(Duration.ofHours(24));
                } catch (Exception e) {
                    log.error("redis set key error", e);
                }

            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendDerivative error", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    /**
     * 更新官方推荐缓存
     */
    public void executeOfficial(int typeId) {
        RLock lock = redissonClient.getLock("travel:precache:official:lock:" + typeId);
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());

                // 缓存 key
                String redisKey = "travel:official:recommend:" + typeId;

                // 判断缓存是否存在，若存在则直接退出
                RList<Object> oldList = redissonClient.getList(redisKey);
                if (CollectionUtils.isNotEmpty(oldList)) {
                    return;
                }

                // 查询推荐的官方列表（分页查询）
                QueryWrapper<Official> officialQueryWrapper = new QueryWrapper<>();
                officialQueryWrapper.eq("type_id", typeId);
                officialQueryWrapper.last("order by 5*like_count+3*favorite_count+view_count+review_count desc limit 50");
                List<Official> officialList = officialService.list(officialQueryWrapper);
                List<OfficialVO> officialVOList = officialService.getOfficialVOList(officialList);

                // 写缓存
                try {
                    // todo: 需要转换成 json 格式再添加吗
                    RList<String> list = redissonClient.getList(redisKey);
                    List<String> officialVOStrList = officialVOList.stream().map(officialVO -> gson.toJson(officialVO)).collect(Collectors.toList());
                    list.addAll(officialVOStrList);
                    list.expire(Duration.ofHours(24));
                } catch (Exception e) {
                    log.error("redis set key error", e);
                }

            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendOfficial error", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    /**
     * 更新官方点赞（点赞量+点赞表）
     * @param loginUserId
     * @param type
     * @param id
     * @param status
     */
    public void executeOfficialLike(long loginUserId, int type, long id, int status) {

        // 使用注册器简化代码
        UpdateWrapper<?> updateWrapper = updateWrapperRegistry.getUpdateWrapperByType(type);
        IService<?> service = serviceRegistry.getServiceByType(type);
        updateWrapper.eq("id", id);

        // 点赞
        if (status == 0) {
            updateWrapper.setSql("like_count = like_count + 1");

            // 将点赞加入用户行为记录中
            innerDataService.addBehavior(loginUserId, type, id, BehaviorTypeConstant.DISLIKE);
        } else if (status == 1) {
            // 取消点赞
            updateWrapper.setSql("like_count = like_count - 1");

            // 将取消点赞加入用户行为记录中
            innerDataService.addBehavior(loginUserId, type, id, BehaviorTypeConstant.LIKE);
        }

        // 执行
        service.update((Wrapper) updateWrapper);

        // 更新用户点赞表
        innerUserService.updateOfficialLike(loginUserId, type, id, status);

    }
}
