package com.travel.travel.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.constant.TypeConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.model.dto.travel.*;
import com.travel.common.model.vo.ArticleVDTO;
import com.travel.common.model.vo.SearchVDTO;
import com.travel.common.model.vo.VideoVDTO;
import com.travel.common.service.InnerTravelService;
import com.travel.common.utils.AlgorithmUtils;
import com.travel.travel.mapper.ArticleMapper;
import com.travel.travel.mapper.VideoMapper;
import com.travel.travel.model.dto.ArticleEsDTO;
import com.travel.travel.model.dto.VideoEsDTO;
import com.travel.travel.model.entity.Article;
import com.travel.travel.model.entity.Video;
import com.travel.travel.model.vo.ArticleVO;
import com.travel.travel.model.vo.VideoVO;
import com.travel.travel.service.ArticleService;
import com.travel.travel.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 6:33
 */
@DubboService
@Slf4j
public class InnerTravelServiceImpl implements InnerTravelService {

    @Resource
    private ArticleService articleService;

    @Resource
    private VideoService videoService;

    @Resource
    private Gson gson;
    
    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public boolean updateTravelByTeamId(Long userId, Long teamId) {
        // 文章游记查询
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("team_id", teamId);
        articleQueryWrapper.select("id");

        // 视频游记查询
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("team_id", teamId);
        videoQueryWrapper.select("id");

        // 更新所有用户的游记
        if (userId == null) {
            List<Article> articleList = articleService.list(articleQueryWrapper);
            List<Video> videoList = videoService.list(videoQueryWrapper);

            if (CollectionUtils.isNotEmpty(articleList)) {
                // 将文章游记的文章 id 移到回收站
                // todo：出错则抛异常（考虑分布式事务）
                boolean update = articleService.updateBatchById(articleList);
                if (update == false) {
                    throw new RuntimeException(ErrorCode.OPERATION_ERROR.getMessage());
                }
            }

            if (CollectionUtils.isNotEmpty(videoList)) {
                // 将视频游记的文章 id 移到回收站
                // todo：出错则抛异常
                boolean update = videoService.updateBatchById(videoList);
                if (update == false) {
                    throw new RuntimeException(ErrorCode.OPERATION_ERROR.getMessage());
                }
            }
        }

        // 更新指定用户的游记
        if (userId != null) {
            articleQueryWrapper.eq("user_id", userId);
            videoQueryWrapper.eq("user_id", userId);
            List<Article> articleList = articleService.list(articleQueryWrapper);
            List<Video> videoList = videoService.list(videoQueryWrapper);

            if (CollectionUtils.isNotEmpty(articleList)) {
                // 将文章游记的文章 id 移到回收站
                // todo：出错则抛异常（考虑分布式事务）
                boolean update = articleService.updateBatchById(articleList);
                if (update == false) {
                    throw new RuntimeException(ErrorCode.OPERATION_ERROR.getMessage());
                }
            }

            if (CollectionUtils.isNotEmpty(videoList)) {
                // 将视频游记的文章 id 移到回收站
                // todo：出错则抛异常
                boolean update = videoService.updateBatchById(videoList);
                if (update == false) {
                    throw new RuntimeException(ErrorCode.OPERATION_ERROR.getMessage());
                }
            }
        }
        return true;
    }

    @Override
    public Page<ArticleVDTO> searchFromEs(ArticleQueryRequest articleQueryRequest) {
        // todo: 从 ES 中搜索数据，并需要从数据库中储存数据
        // ES 中只存储需要搜索的字段
        String searchText = articleQueryRequest.getSearchText();

        // es 起始页为 0
        long current = articleQueryRequest.getCurrent() - 1;
        long pageSize = articleQueryRequest.getPageSize();

        // 获取排序字段
        String sortField = articleQueryRequest.getSortField();
        String sortOrder = articleQueryRequest.getSortOrder();

        // 获取标签列表
        List<String> tagList = new ArrayList<>();
        if (articleQueryRequest.getTag() != null) {
            tagList = gson.fromJson(articleQueryRequest.getTag(), new TypeToken<List<String>>() {}.getType());
        }

        // todo: 若 sortField 为 all，说明走综合排序

        // 构建 bool 查询器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 包含任何一个标签即可
        if (CollectionUtils.isNotEmpty(tagList)) {
            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String tag : tagList) {
                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tag", tag));
            }
            orTagBoolQueryBuilder.minimumShouldMatch(1);
            boolQueryBuilder.filter(orTagBoolQueryBuilder);
        }

        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("intro", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("location", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }

        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);

        // 构造查询（高亮查询）
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .withHighlightFields(new HighlightBuilder.Field("title")).build();

        SearchHits<ArticleEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, ArticleEsDTO.class);

        // 构造 Page 对象
        Page<Article> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        page.setCurrent(current);
        page.setSize(pageSize);
        List<Article> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取更多信息
        if (searchHits.hasSearchHits()) {
            List<SearchHit<ArticleEsDTO>> searchHitList = searchHits.getSearchHits();

            // 构建 ES (id, title) 的 map
            HashMap<Long, String> articleIdTitleMap = new HashMap<>();

            searchHitList.stream().forEach(searchHit ->
                    articleIdTitleMap.put(searchHit.getContent().getId(), searchHit.getHighlightField("title").get(0)));

            // todo: 若 sortField 为 all，说明走综合排序
            // 从数据库中取出更完整的数据（并排序）
            QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.in("id", articleIdTitleMap.keySet());
            if ("all".equals(sortField)) {
                articleQueryWrapper.last("order by 3*like_count+2*comment_count+3*favorite_count+2*view_count desc");
            } else {
                articleQueryWrapper.orderBy(true, CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
            }
            List<Article> articleList = articleMapper.selectList(articleQueryWrapper);

            if (articleList != null) {
                // 将数据库中的文章列表 -> （文章 id，文章列表）
                Map<Long, List<Article>> idArticleMap = articleList.stream().collect(Collectors.groupingBy(Article::getId));

                // 遍历 ES 中的文章 id 列表，剔除数据库已经不存在的文章
                articleIdTitleMap.entrySet().forEach(entry -> {
                    Long articleId = entry.getKey();
                    String highLightTitle = entry.getValue();
                    if (articleIdTitleMap.containsKey(articleId)) {
                        // 将 article 的非高亮字段赋值为高亮的值
                        Article article = idArticleMap.get(articleId).get(0);
                        article.setTitle(highLightTitle);
                        resourceList.add(article);
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(articleId), ArticleEsDTO.class);
                        log.info("delete article {}", delete);
                    }
                });
            }
        }
        // 设置记录值
        page.setRecords(resourceList);

        return articleObjPageToVdtoPage(page);
    }

    @Override
    public Page<VideoVDTO> searchFromEs(VideoQueryRequest videoQueryRequest) {
        // todo: 从 ES 中搜索数据，并需要从数据库中储存数据
        // ES 中只存储需要搜索的字段
        String searchText = videoQueryRequest.getSearchText();

        // es 起始页为 0
        long current = videoQueryRequest.getCurrent() - 1;
        long pageSize = videoQueryRequest.getPageSize();

        // 获取排序字段
        String sortField = videoQueryRequest.getSortField();
        String sortOrder = videoQueryRequest.getSortOrder();

        // 获取标签列表
        List<String> tagList = new ArrayList<>();
        if (videoQueryRequest.getTag() != null) {
            tagList = gson.fromJson(videoQueryRequest.getTag(), new TypeToken<List<String>>() {}.getType());
        }

        // todo: 若 sortField 为 all，说明走综合排序

        // 构建 bool 查询器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 包含任何一个标签即可
        if (CollectionUtils.isNotEmpty(tagList)) {
            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String tag : tagList) {
                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tag", tag));
            }
            orTagBoolQueryBuilder.minimumShouldMatch(1);
            boolQueryBuilder.filter(orTagBoolQueryBuilder);
        }

        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("intro", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("location", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }

        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);

        // 构造查询（高亮查询）
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .withHighlightFields(new HighlightBuilder.Field("title")).build();

        SearchHits<VideoEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, VideoEsDTO.class);

        // 构造 Page 对象
        Page<Video> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        page.setCurrent(current);
        page.setSize(pageSize);
        List<Video> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取更多信息
        if (searchHits.hasSearchHits()) {
            List<SearchHit<VideoEsDTO>> searchHitList = searchHits.getSearchHits();

            // 构建 ES (id, title) 的 map
            HashMap<Long, String> videoIdTitleMap = new HashMap<>();

            searchHitList.stream().forEach(searchHit ->
                    videoIdTitleMap.put(searchHit.getContent().getId(), searchHit.getHighlightField("title").get(0)));

            // todo: 若 sortField 为 all，说明走综合排序
            // 从数据库中取出更完整的数据（并排序）
            QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
            videoQueryWrapper.in("id", videoIdTitleMap.keySet());
            if ("all".equals(sortField)) {
                videoQueryWrapper.last("order by 3*like_count+2*comment_count+3*favorite_count+2*view_count desc");
            } else {
                videoQueryWrapper.orderBy(true, CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
            }
            List<Video> videoList = videoMapper.selectList(videoQueryWrapper);

            if (videoList != null) {
                // 将数据库中的视频列表 -> （视频 id，视频列表）
                Map<Long, List<Video>> idVideoMap = videoList.stream().collect(Collectors.groupingBy(Video::getId));

                // 遍历 ES 中的视频 id 列表，剔除数据库已经不存在的视频
                videoIdTitleMap.entrySet().forEach(entry -> {
                    Long videoId = entry.getKey();
                    String highLightTitle = entry.getValue();
                    if (videoIdTitleMap.containsKey(videoId)) {
                        // 将 article 的非高亮字段赋值为高亮的值
                        Video video = idVideoMap.get(videoId).get(0);
                        video.setTitle(highLightTitle);
                        resourceList.add(video);
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(videoId), VideoEsDTO.class);
                        log.info("delete video {}", delete);
                    }
                });
            }
        }
        // 设置记录值
        page.setRecords(resourceList);

        return videoObjPageToVdtoPage(page);
    }

    @Override
    public SearchVDTO listTravelRcmd(TravelRcmdRequest travelRcmdRequest) {
        // 获取当前页和页大小
        long current = travelRcmdRequest.getCurrent();
        long pageSize = travelRcmdRequest.getPageSize();
        // 推荐类型
        Integer rcmdType = travelRcmdRequest.getRcmdType();
        // 游记类型
        Integer travelType = travelRcmdRequest.getTravelType();
        // 标签
        String tag = travelRcmdRequest.getTag();
        List<String> tagList = gson.fromJson(tag, new TypeToken<List<String>>() {}.getType());

        // todo：是否需要利用景区名、游记标题、用户 id 做推荐因子

        // 查询景区的相关游记
        if (TypeConstant.OFFICIAL.getTypeIndex().equals(rcmdType)) {
            // 文章游记
            if (TypeConstant.ARTICLE.getTypeIndex().equals(travelType)) {
                // 排序文章
                return sortArticleList(tagList, current, pageSize);
            }

            // 视频游记
            return sortVideoList(tagList, current, pageSize);
        }

        // 查询文章游记的相关游记
        if (TypeConstant.ARTICLE.getTypeIndex().equals(rcmdType)) {
            return sortArticleList(tagList, current, pageSize);
        }

        // 查询视频游记的相关游记
        if (TypeConstant.VIDEO.getTypeIndex().equals(rcmdType)) {
            return sortVideoList(tagList, current, pageSize);
        }

        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    @Override
    public List<ArticleDTO> listArticleForReward() {
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.select("id", "user_id", "like_count", "comment_count", "favorite_count");
        List<ArticleDTO> articleDTOList = articleService.list(articleQueryWrapper).stream().map(article -> {
            ArticleDTO articleDTO = new ArticleDTO();
            BeanUtils.copyProperties(article, articleDTO);
            return articleDTO;
        }).collect(Collectors.toList());
        return articleDTOList;
    }

    @Override
    public List<VideoDTO> listVideoForReward() {
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.select("id", "user_id", "like_count", "comment_count", "favorite_count");
        List<VideoDTO> videoDTOList = videoService.list(videoQueryWrapper).stream().map(video -> {
            VideoDTO videoDTO  = new VideoDTO();
            BeanUtils.copyProperties(video, videoDTO);
            return videoDTO;
        }).collect(Collectors.toList());
        return videoDTOList;
    }

    /**
     * 排序文章
     * @param tagList
     * @param current
     * @param pageSize
     * @return
     */
    private SearchVDTO sortArticleList(List<String> tagList, long current, long pageSize) {
        // 保存每个文章 id 与 相似度的 list
        List<Pair<Long, Long>> list = new ArrayList<>();
        // 查询结果
        SearchVDTO searchVDTO = new SearchVDTO();
        // 构造器
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        // 第一次仅查询 id，tag
        articleQueryWrapper.select("id", "tag");
        List<Article> articleList = articleService.list(articleQueryWrapper);
        // 计算每个文章和该景区的标签匹配度
        for (Article article : articleList) {
            String articleTag = article.getTag();
            List<String> articleTagList = gson.fromJson(articleTag, new TypeToken<List<String>>() {}.getType());
            // 计算两者的相似分数
            long distance = AlgorithmUtils.minDistance(tagList, articleTagList);
            list.add(new Pair<>(article.getId(), distance));
        }

        // 获取匹配度较高的前 num 个文章 id
        List<Long> topArticleIdList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(pageSize)
                .map(articleIdDistance -> articleIdDistance.getKey())
                .collect(Collectors.toList());

        // 查询最终需要返回的文章，转换成 Page<ArticleVDTO>
        List<Article> finalArticleList = articleService.listByIds(topArticleIdList);
        Page<Article> articlePage = new Page<>(current, pageSize, articleList.size());
        articlePage.setRecords(finalArticleList);
        Page<ArticleVDTO> articleVDTOPage = articleObjPageToVdtoPage(articlePage);
        searchVDTO.setDataPage(articleVDTOPage);
        return searchVDTO;
    }

    /**
     * 排序视频
     * @param tagList
     * @param current
     * @param pageSize
     * @return
     */
    private SearchVDTO sortVideoList(List<String> tagList, long current, long pageSize) {
        // 保存每个视频 id 与 相似度的 list
        List<Pair<Long, Long>> list = new ArrayList<>();
        // 查询结果
        SearchVDTO searchVDTO = new SearchVDTO();
        // 构造器
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        // 第一次仅查询 id，tag
        videoQueryWrapper.select("id", "tag");
        List<Video> videoList = videoService.list(videoQueryWrapper);
        // 计算每个视频和该景区的标签匹配度
        for (Video video : videoList) {
            String videoTag = video.getTag();
            List<String> videoTagList = gson.fromJson(videoTag, new TypeToken<List<String>>() {}.getType());
            // 计算两者的相似分数
            long distance = AlgorithmUtils.minDistance(tagList, videoTagList);
            list.add(new Pair<>(video.getId(), distance));
        }

        // 获取匹配度较高的前 num 个视频 id
        List<Long> topVideoIdList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(pageSize)
                .map(videoIdDistance -> videoIdDistance.getKey())
                .collect(Collectors.toList());

        // 查询最终需要返回的视频，转换成 Page<VideoVDTO>
        List<Video> finalVideoList = videoService.listByIds(topVideoIdList);
        Page<Video> videoPage = new Page<>(current, pageSize, videoList.size());
        videoPage.setRecords(finalVideoList);
        Page<VideoVDTO> videoVDTOPage = videoObjPageToVdtoPage(videoPage);
        searchVDTO.setDataPage(videoVDTOPage);
        return searchVDTO;
    }

    /**
     * 将实体 page 转换成 VDTO page
     * @param articlePage
     * @return
     */
    private Page<ArticleVDTO> articleObjPageToVdtoPage(Page<Article> articlePage) {
        // articlePage -> articleVDTOPage
        // 将 articlePage -> articleVOPage -> articleVOList
        List<ArticleVO> articleVOList = articleService.getArticleVOPage(articlePage).getRecords();

        Page<ArticleVDTO> articleVDTOPage = new Page<>(articlePage.getCurrent(), articlePage.getSize());

        // 将 articleVOList -> articleVDTOList
        List<ArticleVDTO> articleVDTOList = articleVOList.stream().map(articleVO -> {
            ArticleVDTO articleVDTO = new ArticleVDTO();
            BeanUtils.copyProperties(articleVO, articleVDTO);
            return articleVDTO;
        }).collect(Collectors.toList());

        // 为 articleVDTOPage 注入属性
        articleVDTOPage.setTotal(articlePage.getTotal());
        articleVDTOPage.setRecords(articleVDTOList);

        return articleVDTOPage;
    }

    /**
     * 将实体 page 转换成 VDTO page
     * @param videoPage
     * @return
     */
    private Page<VideoVDTO> videoObjPageToVdtoPage(Page<Video> videoPage) {
        // articlePage -> articleVDTOPage
        // 将 articlePage -> articleVOPage -> articleVOList
        List<VideoVO> videoVOList = videoService.getVideoVOPage(videoPage).getRecords();

        Page<VideoVDTO> videoVDTOPage = new Page<>(videoPage.getCurrent(), videoPage.getSize());

        // 将 articleVOList -> articleVDTOList
        List<VideoVDTO> articleVDTOList = videoVOList.stream().map(videoVO -> {
            VideoVDTO videoVDTO = new VideoVDTO();
            BeanUtils.copyProperties(videoVO, videoVDTO);
            return videoVDTO;
        }).collect(Collectors.toList());

        // 为 videoVDTOPage 注入属性
        videoVDTOPage.setTotal(videoPage.getTotal());
        videoVDTOPage.setRecords(articleVDTOList);

        return videoVDTOPage;
    }
}
