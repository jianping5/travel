package com.travel.travel.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.travel.common.common.ErrorCode;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.service.InnerTravelService;
import com.travel.travel.model.entity.Article;
import com.travel.travel.model.entity.Video;
import com.travel.travel.service.ArticleService;
import com.travel.travel.service.VideoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 6:33
 */
@DubboService
public class InnerTravelServiceImpl implements InnerTravelService {

    @Resource
    private ArticleService articleService;

    @Resource
    private VideoService videoService;

    @Override
    public boolean updateTravelByTeamId(Long userId, Long teamId) {

        // 文章游记
        UpdateWrapper<Article> articleUpdateWrapper = new UpdateWrapper<>();
        articleUpdateWrapper.eq("team_id", teamId);
        articleUpdateWrapper.setSql("team_id = 0");

        // 视频游记
        UpdateWrapper<Video> videoUpdateWrapper = new UpdateWrapper<>();
        videoUpdateWrapper.eq("team_id", teamId);
        videoUpdateWrapper.setSql("team_id = 0");

        // 更新所有用户的游记
        if (userId == null) {
            // 将文章游记的团队 id 移到回收站
            boolean updateArticle = articleService.update(articleUpdateWrapper);
            ThrowUtils.throwIf(!updateArticle, ErrorCode.OPERATION_ERROR);

            // 将视频游记的团队 id 移到回收站
            boolean updateVideo = videoService.update(videoUpdateWrapper);
            ThrowUtils.throwIf(!updateVideo, ErrorCode.OPERATION_ERROR);
        }

        // 更新指定用户的游记
        if (userId != null) {
            articleUpdateWrapper.eq("user_id", userId);
            videoUpdateWrapper.eq("user_id", userId);

            // 将指定用户的文章游记的团队 id 移到回收站
            boolean updateArticle = articleService.update(articleUpdateWrapper);
            ThrowUtils.throwIf(!updateArticle, ErrorCode.OPERATION_ERROR);

            // 将指定用户的视频游记的团队 id 移到回收站
            boolean updateVideo = videoService.update(videoUpdateWrapper);
            ThrowUtils.throwIf(!updateVideo, ErrorCode.OPERATION_ERROR);
        }


        return true;
    }
}
