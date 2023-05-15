package com.travel.travel.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.travel.travel.model.entity.Article;
import com.travel.travel.model.vo.ArticleVO;
import com.travel.travel.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
    private ArticleService articleService;

    @Resource
    private Gson gson;

    /**
     * 更新推荐团队缓存
     */
    public void execute() {
        RLock lock = redissonClient.getLock("travel:precache:travel:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());

                // 缓存 key
                String redisKey = "travel:travel:recommend";

                // 判断缓存是否存在，若存在则直接退出
                RList<Object> oldList = redissonClient.getList(redisKey);
                if (CollectionUtils.isNotEmpty(oldList)) {
                    return;
                }

                // 查询推荐的团队列表（分页查询）
                QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
                articleQueryWrapper.last("order by 5*like_count+3*favorite_count+2*view_count desc limit 50");
                List<Article> articleList = articleService.list(articleQueryWrapper);
                List<ArticleVO> articleVOList = articleService.getArticleVOList(articleList);

                // 写缓存
                try {
                    // todo: 需要转换成 json 格式再添加吗  写缓存之前需要删除吗？
                    RList<String> list = redissonClient.getList(redisKey);
                    List<String> articleVOStrList = articleVOList.stream().map(articleVO -> gson.toJson(articleVO)).collect(Collectors.toList());
                    list.addAll(articleVOStrList);
                    list.expire(Duration.ofHours(8));
                } catch (Exception e) {
                    log.error("redis set key error", e);
                }

            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendArticle error", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
