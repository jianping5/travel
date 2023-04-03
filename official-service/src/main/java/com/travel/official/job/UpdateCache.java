package com.travel.official.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.travel.official.model.entity.Derivative;
import com.travel.official.model.vo.DerivativeVO;
import com.travel.official.service.DerivativeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.support.GenericApplicationContext;
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
    private Gson gson;

    /**
     * 更新缓存
     */
    public void execute() {
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
}
