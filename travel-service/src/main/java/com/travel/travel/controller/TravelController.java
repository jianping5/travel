package com.travel.travel.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.travel.model.entity.Article;
import com.travel.travel.model.entity.ArticleDetail;
import com.travel.travel.model.entity.Video;
import com.travel.travel.model.request.*;
import com.travel.travel.model.vo.ArticleVO;
import com.travel.travel.model.vo.VideoVO;
import com.travel.travel.service.ArticleDetailService;
import com.travel.travel.service.ArticleService;
import com.travel.travel.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 3:10
 */
@RestController
@Api(tags = "Travel 控制器")
public class TravelController {
    @Resource
    private ArticleService articleService;
    @Resource
    private VideoService videoService;
    @Resource
    private ArticleDetailService articleDetailService;

    @PostMapping("/article/add")
    @ApiOperation(value = "发布文章游记")
    public BaseResponse<Long> addArticle(@RequestBody ArticleAddRequest articleAddRequest) {
        // 校验请求体
        if (articleAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 Article 中
        Article article = new Article();
        BeanUtils.copyProperties(articleAddRequest, article);
        User user = UserHolder.getUser();
        article.setUserId(user.getId());

        // 校验 Article 信息是否合法
        articleService.validArticle(article, true);

        // 添加周边
        Article newArticle = articleService.addArticle(article);

        // 获取周边 id
        long newArticleId = newArticle.getId();

        return ResultUtils.success(newArticleId);
    }

    @PostMapping("/article/delete")
    @ApiOperation(value = "删除文章游记")
    public BaseResponse<Boolean> deleteArticle(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 Article id
        long id = deleteRequest.getId();

        // 判断是否存在
        Article oldPost = articleService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = articleService.deleteArticle(oldPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    @PostMapping("/article/update")
    @ApiOperation(value = "更新文章游记")
    public BaseResponse<Boolean> updateArticle(@RequestBody ArticleUpdateRequest articleUpdateRequest) {
        // 校验团队更新请求体
        if (articleUpdateRequest == null || articleUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将团队更新请求体的内容赋值给 团队
        Article Article = new Article();
        BeanUtils.copyProperties(articleUpdateRequest, Article);

        // 参数校验
        articleService.validArticle(Article, false);
        long id = articleUpdateRequest.getId();

        // 判断是否存在
        Article oldPost = articleService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新团队
        articleService.updateArticle(Article);

        return ResultUtils.success(true);
    }

    @GetMapping("/article/get/vo")
    @ApiOperation(value = "根据Id查询文章游记")
    public BaseResponse<ArticleVO> getArticleVOById(Long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Article article = articleService.getById(id);
        if(article!=null){
            article.setViewCount(article.getViewCount()+1);
            articleService.updateById(article);
        }
        ArticleDetail articleDetail = articleDetailService.getOne(new QueryWrapper<ArticleDetail>().eq("article_id", id));
        if (article == null||articleDetail==null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // todo: 插入用户行为记录
        // todo: 插入浏览记录
        return ResultUtils.success(articleService.getArticleVO(article,articleDetail));
    }

    @PostMapping("/article/list/page/vo")
    @ApiOperation(value = "分页查询文章游记")
    public BaseResponse<Page<ArticleVO>> listArticleVOByPage(@RequestBody ArticleQueryRequest articleQueryRequest) {
        if (articleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long size = articleQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Article> articlePage = articleService.queryArticle(articleQueryRequest);

        return ResultUtils.success(articleService.getArticleVOPage(articlePage));
    }

    @GetMapping("/detail")
    @ApiOperation(value = "获取文章详情")
    public BaseResponse<ArticleVO> getArticleDetailVO(long articleId, long detailId) {
        // 校验 id
        if (articleId <= 0 || detailId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取官方，并将官方详情 id 注入
        Article article = articleService.getById(articleId);
        article.setDetailId(detailId);

        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(articleService.getArticleDetailVO(article));
    }




    @PostMapping("/video/add")
    @ApiOperation(value = "发布视频游记")
    public BaseResponse<Long> addVideo(@RequestBody VideoAddRequest videoAddRequest) {
        // 校验请求体
        if (videoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 Video 中
        Video video = new Video();
        BeanUtils.copyProperties(videoAddRequest, video);
        video.setUserId(UserHolder.getUser().getId());

        // 校验 Video 信息是否合法
        videoService.validVideo(video, true);

        // 添加周边
        Video newVideo = videoService.addVideo(video);

        // 获取周边 id
        long newVideoId = newVideo.getId();

        return ResultUtils.success(newVideoId);
    }

    @PostMapping("/video/delete")
    @ApiOperation(value = "删除视频游记")
    public BaseResponse<Boolean> deleteVideo(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 Video id
        long id = deleteRequest.getId();

        // 判断是否存在
        Video oldPost = videoService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = videoService.deleteVideo(oldPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    @PostMapping("/video/update")
    @ApiOperation(value = "更新视频游记")
    public BaseResponse<Boolean> updateVideo(@RequestBody VideoUpdateRequest videoUpdateRequest) {
        // 校验团队更新请求体
        if (videoUpdateRequest == null || videoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将团队更新请求体的内容赋值给 团队
        Video Video = new Video();
        BeanUtils.copyProperties(videoUpdateRequest, Video);

        // 参数校验
        videoService.validVideo(Video, false);
        long id = videoUpdateRequest.getId();

        // 判断是否存在
        Video oldPost = videoService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新团队
        videoService.updateVideo(Video);

        return ResultUtils.success(true);
    }

    @GetMapping("/video/get/vo")
    @ApiOperation(value = "根据Id查询视频游记")
    public BaseResponse<VideoVO> getVideoVOById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //获取视频游记
        Video video = videoService.getById(id);
        if(video!=null){
            video.setViewCount(video.getViewCount()+1);
            videoService.updateById(video);
        }
        if (video == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        //获取当前用户
        return ResultUtils.success(videoService.getVideoDetail(video));
    }

    @PostMapping("/video/list/page/vo")
    @ApiOperation(value = "分页查询视频游记")
    public BaseResponse<Page<VideoVO>> listVideoVOByPage(@RequestBody VideoQueryRequest videoQueryRequest) {
        if (videoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long size = videoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Video> videoPage = videoService.queryVideo(videoQueryRequest);

        //todo: 添加历史记录
        return ResultUtils.success(videoService.getVideoVOPage(videoPage));
    }

    /**
     * 获取推荐游记（大众化推荐）
     *
     * @param articleQueryRequest
     * @return
     */
    @ApiOperation(value = "获取推荐游记（大众化推荐）")
    @PostMapping("/article/rcmd")
    public BaseResponse<List<ArticleVO>> listRcmdArticleVO(@RequestBody ArticleQueryRequest articleQueryRequest) {
        if (articleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = articleQueryRequest.getCurrent();
        long size = articleQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        List<ArticleVO> articleVOList = articleService.listRcmdArticleVO(current, size);

        return ResultUtils.success(articleVOList);
    }
}
