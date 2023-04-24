package com.travel.reward.job.cycle;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.travel.common.constant.TypeConstant;
import com.travel.common.model.dto.travel.ArticleDTO;
import com.travel.common.model.dto.travel.VideoDTO;
import com.travel.common.model.dto.user.UpdateTokenRequest;
import com.travel.common.service.InnerTravelService;
import com.travel.common.service.InnerUserService;
import com.travel.reward.model.entity.RewardRecord;
import com.travel.reward.service.RewardRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 增量同步团队到 es
 *
 * @author jianping5
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class SendRewardTask {

    @DubboReference
    private InnerTravelService innerTravelService;

    @Resource
    private RewardRecordService rewardRecordService;

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private Gson gson;

    /**
     * 每天执行一次（查询游记进行奖励颁发）
     */
    @Scheduled(cron = "0 31 2 * * *")
    public void run() {
        // 分别查询文章和视频
        List<ArticleDTO> articleDTOList = innerTravelService.listArticleForReward();
        List<VideoDTO> videoDTOList = innerTravelService.listVideoForReward();

        // 文章奖励机制
        sendRewardWithArticle(articleDTOList);

        // 视频奖励机制
        sendRewardWithVideo(videoDTOList);
    }

    /**
     * 奖励策略
     * @param likeCount
     * @param commentCount
     * @param favoriteCount
     * @return
     */
    private int computeReward(Integer likeCount, Integer commentCount, Integer favoriteCount) {
        // todo：制定奖励策略
        int rewardCount = (int) ((likeCount * 0.35 + commentCount * 0.15 + favoriteCount * 0.5) * 0.1);
        return rewardCount;
    }

    /**
     * 奖励机制
     * @param articleDTOList
     */
    private void sendRewardWithArticle(List<ArticleDTO> articleDTOList) {
        // todo：奖励机制
        // 查询所有发布的文章对应的 [文章 id 集合]
        Set<Long> articleIdSet = articleDTOList.stream().map(articleDTO -> articleDTO.getId()).collect(Collectors.toSet());

        // 根据 [文章 id 集合] 查询出所有的 奖励记录
        QueryWrapper<RewardRecord> rewardRecordQueryWrapper = new QueryWrapper<>();
        rewardRecordQueryWrapper.in(CollectionUtils.isNotEmpty(articleIdSet), "reward_obj_id", articleIdSet);
        rewardRecordQueryWrapper.eq("reward_obj_type", TypeConstant.ARTICLE.getTypeIndex());
        // [已经存在的奖励对象 id，奖励记录]  map
        Map<Long, List<RewardRecord>> articleIdRewardRecordMap = rewardRecordService.list(rewardRecordQueryWrapper).stream()
                .collect(Collectors.groupingBy(RewardRecord::getRewardObjId));

        // 遍历 [文章列表]
        articleDTOList.stream().forEach(articleDTO -> {
            Long userId = articleDTO.getUserId();
            Long id = articleDTO.getId();
            Integer likeCount = articleDTO.getLikeCount();
            Integer commentCount = articleDTO.getCommentCount();
            Integer favoriteCount = articleDTO.getFavoriteCount();
            Integer rewardCount = 0;

            // 之前未包含奖励记录
            if (!articleIdRewardRecordMap.containsKey(id)) {
                // 初始化 奖励记录
                RewardRecord rewardRecord = new RewardRecord();
                rewardRecord.setUserId(userId);
                rewardRecord.setRewardObjType(TypeConstant.ARTICLE.getTypeIndex());
                rewardRecord.setRewardObjId(id);
                rewardRecord.setLikeCount(likeCount);
                rewardRecord.setCommentCount(commentCount);
                rewardRecord.setCollectCount(favoriteCount);
                rewardCount = computeReward(likeCount, commentCount, favoriteCount);
                rewardRecord.setRewardCount(rewardCount);
                rewardRecordService.save(rewardRecord);
            }

            // 之前已经包含奖励记录
            if (articleIdRewardRecordMap.containsKey(id)) {
                // 获取旧的奖励记录
                RewardRecord oldrewardRecord = articleIdRewardRecordMap.get(id).get(0);
                // 获取新增的点赞量等
                int incLikeCount = likeCount - oldrewardRecord.getLikeCount();
                int incCommentCount = commentCount - oldrewardRecord.getCommentCount();
                int incFavoriteCount = favoriteCount - oldrewardRecord.getCommentCount();
                // 计算奖励金额
                rewardCount = computeReward(incLikeCount, incCommentCount, incFavoriteCount);
                // 获取新的奖励金额
                int newRewardCount = oldrewardRecord.getRewardCount() + rewardCount;
                // 更新奖励记录
                oldrewardRecord.setLikeCount(likeCount);
                oldrewardRecord.setCommentCount(commentCount);
                oldrewardRecord.setCollectCount(favoriteCount);
                oldrewardRecord.setRewardCount(newRewardCount);

                rewardRecordService.updateById(oldrewardRecord);
            }

            // 添加到用户的账户中 消息队列
            String exchangeName = "travel.topic";
            UpdateTokenRequest updateTokenRequest = new UpdateTokenRequest();
            updateTokenRequest.setUserId(userId);
            updateTokenRequest.setIsAdd(true);
            updateTokenRequest.setToken(rewardCount);
            String updateTokenRequestJson = gson.toJson(updateTokenRequest);
            rabbitTemplate.convertAndSend(exchangeName, "token.add", updateTokenRequestJson);

        });
    }

    /**
     * 视频奖励机制
     * @param videoDTOList
     */
    private void sendRewardWithVideo(List<VideoDTO> videoDTOList) {
        // todo：奖励机制
        // 查询所有发布的视频对应的 [视频 id 集合]
        Set<Long> videoIdSet = videoDTOList.stream().map(videoDTO -> videoDTO.getId()).collect(Collectors.toSet());

        // 根据 [视频 id 集合] 查询奖励记录
        QueryWrapper<RewardRecord> rewardRecordQueryWrapper = new QueryWrapper<>();
        rewardRecordQueryWrapper.in(CollectionUtils.isNotEmpty(videoIdSet), "reward_obj_id", videoIdSet);
        rewardRecordQueryWrapper.eq("reward_obj_type", TypeConstant.VIDEO.getTypeIndex());
        // [已经存在的奖励对象 id，奖励记录]  map
        Map<Long, List<RewardRecord>> videoIdRewardRecordMap = rewardRecordService.list(rewardRecordQueryWrapper).stream()
                .collect(Collectors.groupingBy(RewardRecord::getRewardObjId));

        // 遍历 [视频列表]
        videoDTOList.stream().forEach(videoDTO -> {
            Long userId = videoDTO.getUserId();
            Long id = videoDTO.getId();
            Integer likeCount = videoDTO.getLikeCount();
            Integer commentCount = videoDTO.getCommentCount();
            Integer favoriteCount = videoDTO.getFavoriteCount();
            Integer rewardCount = 0;


            // 之前未包含奖励记录
            if (!videoIdRewardRecordMap.containsKey(id)) {
                // 初始化 奖励记录
                RewardRecord rewardRecord = new RewardRecord();
                rewardRecord.setUserId(userId);
                rewardRecord.setRewardObjType(TypeConstant.VIDEO.getTypeIndex());
                rewardRecord.setRewardObjId(id);
                rewardRecord.setLikeCount(likeCount);
                rewardRecord.setCommentCount(commentCount);
                rewardRecord.setCollectCount(favoriteCount);
                rewardCount = computeReward(likeCount, commentCount, favoriteCount);
                rewardRecord.setRewardCount(rewardCount);
                rewardRecordService.save(rewardRecord);
            }

            // 之前已经包含奖励记录
            if (videoIdRewardRecordMap.containsKey(id)) {
                // 获取旧的奖励记录
                RewardRecord oldrewardRecord = videoIdRewardRecordMap.get(id).get(0);
                // 获取新增的点赞量等
                int incLikeCount = likeCount - oldrewardRecord.getLikeCount();
                int incCommentCount = commentCount - oldrewardRecord.getCommentCount();
                int incFavoriteCount = favoriteCount - oldrewardRecord.getCommentCount();
                // 计算奖励金额
                rewardCount = computeReward(incLikeCount, incCommentCount, incFavoriteCount);
                // 获取新的奖励金额
                int newRewardCount = oldrewardRecord.getRewardCount() + rewardCount;
                // 更新奖励记录
                oldrewardRecord.setLikeCount(likeCount);
                oldrewardRecord.setCommentCount(commentCount);
                oldrewardRecord.setCollectCount(favoriteCount);
                oldrewardRecord.setRewardCount(newRewardCount);

                rewardRecordService.updateById(oldrewardRecord);
            }

            // 添加到用户的账户中 消息队列
            String exchangeName = "travel.topic";
            UpdateTokenRequest updateTokenRequest = new UpdateTokenRequest();
            updateTokenRequest.setUserId(userId);
            updateTokenRequest.setIsAdd(true);
            updateTokenRequest.setToken(rewardCount);
            String updateTokenRequestJson = gson.toJson(updateTokenRequest);
            rabbitTemplate.convertAndSend(exchangeName, "token.add", updateTokenRequestJson);

        });
    }
}
