package com.travel.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.BehaviorTypeConstant;
import com.travel.common.constant.TypeConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.UserHolder;
import com.travel.travel.mapper.ArticleMapper;
import com.travel.travel.model.entity.Article;
import com.travel.travel.model.entity.ArticleDetail;
import com.travel.travel.model.request.ArticleQueryRequest;
import com.travel.travel.model.vo.ArticleVO;
import com.travel.travel.service.ArticleDetailService;
import com.travel.travel.service.ArticleService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RList;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【article(文章表)】的数据库操作Service实现
* @createDate 2023-03-24 19:23:06
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService , com.travel.common.service.ArticleService {

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private ArticleDetailService articleDetailService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private Gson gson;

    @Override
    public void validArticle(Article article, boolean add) {
        if (article == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = article.getUserId();
        String intro = article.getIntro();
        String coverUrl = article.getCoverUrl();
        String tag = article.getTag();
        String detail = article.getDetail();
        String title = article.getTitle();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(coverUrl,detail,tag,intro,title), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.anyNull(userId,intro,coverUrl,tag,detail),ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(detail) && detail.length() > 2000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "详情过长");
        }
    }

    @Override
    public ArticleVO getArticleVO(Article article, ArticleDetail articleDetail) {
        ArticleVO articleVO = ArticleVO.objToVo(article,articleDetail);

        //查询用户信息
        Long userId = article.getUserId();
        UserDTO user = innerUserService.getUser(userId);
        if(user!=null){
            articleVO.setUserName(user.getUserName());
            articleVO.setUserAvatar(user.getUserAvatar());
        }else {
            articleVO.setUserName("用户不存在");
            articleVO.setUserAvatar("");
        }
        //获取当前登录对象
        User loginUser = UserHolder.getUser();
        if(loginUser!=null){
            Long loginUserId = loginUser.getId();

            // 注入点赞状态
            RSet<Long> likeSet = redissonClient.getSet("travel:user:like:" + TypeConstant.ARTICLE + ":" + article.getId());
            articleVO.setIsLiked(likeSet.contains(loginUserId)?1:0);

            // 注入收藏状态
            RSet<Long> collectSet = redissonClient.getSet("travel:user:collection:" + TypeConstant.ARTICLE + ":" + article.getId());
            articleVO.setIsCollected(collectSet.contains(loginUserId)?1:0);

            // 注入关注状态
            RSet<Long> followSet = redissonClient.getSet("travel:user:follow:"+ article.getUserId());
            articleVO.setIsFollowed(followSet.contains(loginUserId)?1:0);
        }
        return articleVO;
    }

    @Override
    public ArticleVO getArticleDetailVO(Article article) {

        //获取详情
        ArticleDetail articleDetail = articleDetailService.getById(article.getDetailId());

        //注入视图体
        ArticleVO articleVO = getArticleVO(article, articleDetail);

        //获取当前登录对象
        User loginUser = UserHolder.getUser();
        if(loginUser!=null){
            Long userId = loginUser.getId();

            // 插入历史记录
            String historyMsg = userId+","+TypeConstant.ARTICLE.getTypeIndex()+","+article.getId();
            rabbitTemplate.convertAndSend("user.history.execute",historyMsg);

            // 加入用户行为记录表（消息队列）
            String behaviorMsg = userId+","+TypeConstant.ARTICLE.getTypeIndex()+","+article.getId()+","+BehaviorTypeConstant.VIEW.getTypeIndex();
            rabbitTemplate.convertAndSend("user.behavior.execute",behaviorMsg);
        }
        return articleVO;
    }
    @Override
    public Page<ArticleVO> getArticleVOPage(Page<Article> articlePage) {
        if(articlePage == null){
            return null;
        }
        List<Article> articleList = articlePage.getRecords();
        Page<ArticleVO> articleVOPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        if (CollectionUtils.isEmpty(articleList)) {
            return articleVOPage;
        }
        //填充信息
        List<ArticleVO> articleVOList = getArticleVOList(articleList);
        articleVOPage.setRecords(articleVOList);
        return articleVOPage;
    }

    /**
     * 根据文章列表获取文章视图体列表
     * @param articleList
     * @return
     */
    @Override
    public List<ArticleVO> getArticleVOList(List<Article> articleList) {
        // 获取用户 id 列表
        Set<Long> userIdSet = articleList.stream().map(Article::getUserId).collect(Collectors.toSet());

        // 获取用户列表
        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);

        // 将用户 id 和用户对应起来
        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));

        // 获取 map（官方 id，官方详情 List）
        Set<Long> articleIdSet = articleList.stream().map(article -> article.getId()).collect(Collectors.toSet());
        QueryWrapper<ArticleDetail> articleDetailQueryWrapper = new QueryWrapper<>();
        articleDetailQueryWrapper.in(org.apache.commons.collections4.CollectionUtils.isNotEmpty(articleIdSet), "article_id", articleIdSet);
        List<ArticleDetail> articleDetailList = articleDetailService.list(articleDetailQueryWrapper);
        Map<Long, List<ArticleDetail>> articleIdDetailListMap = articleDetailList.stream().collect(Collectors.groupingBy(ArticleDetail::getArticleId));

        // 填充信息
        List<ArticleVO> articleVOList = articleList.stream().map(article -> {
            // 获取官方视图体
            ArticleVO articleVO = ArticleVO.objToVo(article,null);
            // 注入用户到官方视图体内
            Long userId = article.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            articleVO.setUserAvatar(userDTO.getUserAvatar());
            articleVO.setUserName(userDTO.getUserName());

            // 注入官方详情 id 到官方视图体内
            Long articleId = article.getId();
            ArticleDetail articleDetail = null;
            if (articleIdDetailListMap.containsKey(articleId)) {
                articleDetail = articleIdDetailListMap.get(articleId).get(0);
            }
            articleVO.setDetailId(articleDetail.getId());
            //获取当前登录对象
            /*User loginUser = UserHolder.getUser();
            if(loginUser!=null){
                Long loginUserId = loginUser.getId();

                // 注入点赞状态
                RSet<Long> likeSet = redissonClient.getSet("travel:user:like:" + TypeConstant.ARTICLE + ":" + article.getId());
                articleVO.setIsLiked(likeSet.contains(loginUserId)?1:0);

                // 注入收藏状态
                RSet<Long> collectSet = redissonClient.getSet("travel:user:collection:" + TypeConstant.ARTICLE + ":" + article.getId());
                articleVO.setIsCollected(collectSet.contains(loginUserId)?1:0);

                // 注入关注状态
                RSet<Long> followSet = redissonClient.getSet("travel:user:follow:"+ article.getUserId());
                articleVO.setIsFollowed(followSet.contains(loginUserId)?1:0);
            }*/
            return articleVO;
        }).collect(Collectors.toList());

        return articleVOList;
    }

    @Override
    public List<ArticleVO> listRcmdArticleVO(long current, long size) {
        // 创建团队视图体数组
        List<ArticleVO> articleVOList = new ArrayList<>();

        // 判断有无缓存
        RList<String> articleVORList = redissonClient.getList("travel:travel:recommend");

        // 若有，则直接从缓存中读取
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(articleVORList)) {
            for (long i = current; i < size; i++) {
                String json = articleVORList.get((int) i);
                ArticleVO articleVO = gson.fromJson(json, ArticleVO.class);
                articleVOList.add(articleVO);
            }
            return articleVOList;
        }

        // 若无，则从数据库中读取
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.last("order by 5*like_count+3*favorite_count+2*view_count desc limit 50");

        // 根据团队列表获取团队视图体列表
        List<Article> articleList = this.list(articleQueryWrapper);
        articleVOList = getArticleVOList(articleList);

        // todo: 并将写缓存的任务添加到消息队列
        // 定义交换机名称
        String exchangeName = "travel.topic";

        // 定义消息
        String message = "cache.travel";

        // 发送消息，让对应线程将数据写入缓存
        rabbitTemplate.convertAndSend(exchangeName, "cache.travel", message);

        return articleVOList;
    }

    @Override
    public Page<Article> queryArticle(ArticleQueryRequest articleQueryRequest) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        if (articleQueryRequest == null) {
            return null;
        }
        // 获取查询条件中的字段
        List<Long> idList = articleQueryRequest.getIdList();
        String searchText = articleQueryRequest.getSearchText();
        Long userId = articleQueryRequest.getUserId();
        Integer permission = articleQueryRequest.getPermission();
        Long teamId = articleQueryRequest.getTeamId();
        Integer queryType = articleQueryRequest.getQueryType();
        Integer articleState = articleQueryRequest.getArticleState();
        Long pageSize = articleQueryRequest.getPageSize();
        Long current = articleQueryRequest.getCurrent();
        Integer orderType = articleQueryRequest.getOrderType();
        //id限制
        if (!ObjectUtils.anyNull(idList)) {
            queryWrapper.in(ObjectUtils.isNotEmpty(idList),"id", idList);
        }
        //内容模糊限制
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(i->i.like("intro", searchText).or().like("title", searchText));
        }
        //文章状态限制
        queryWrapper.eq(ObjectUtils.isNotEmpty(articleState),"article_state", articleState);
        //发布者限制
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        //权限限制
        queryWrapper.eq(ObjectUtils.isNotEmpty(permission), "permission", permission);
        //所属团队限制
        queryWrapper.eq(ObjectUtils.isNotEmpty(teamId), "team_id", teamId);
        //分类限制
        if(queryType!=null&&queryType.equals(0)){
            //旅行游记
            queryWrapper.like("tag","旅行");

        }else if(queryType!=null&&queryType.equals(1)){
            //官方游记
            queryWrapper.like("tag","官方");

        }else if (queryType!=null&&queryType.equals(2)){
            //旅游攻略
            queryWrapper.like("tag","攻略");
        }
        //排序限制
        if(orderType!=null&&orderType.equals(0)){
            //热门推荐
            queryWrapper.last("order by 0.3*like_count + 0.3 * comment_count + 0.3*favorite_count + 0.1*view_count desc");

        }else if(orderType!=null&&orderType.equals(1)){
            //最新发布
            queryWrapper.orderByDesc("update_time");
        }
        return page(new Page<>(current, pageSize), queryWrapper);
    }

    @Override
    public Article addArticle(Article article) {

        //todo:考虑事务
        // 添加到数据库中
        article.setViewCount(RandomUtils.nextInt(0, 1000));
        article.setFavoriteCount(RandomUtils.nextInt(0, 1000));

        boolean saveResult = this.save(article);
        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setDetail(article.getDetail());
        articleDetail.setArticleId(article.getId());
        boolean save = articleDetailService.save(articleDetail);
        ThrowUtils.throwIf(!saveResult||!save, ErrorCode.OPERATION_ERROR);
        // 获取该文章
        Article newArticle = this.getById(article.getId());
        if(newArticle!=null){
            ArticleDetail detailById = articleDetailService.getById(articleDetail.getId());
            if(detailById!=null){
                newArticle.setDetail(detailById.getDetail());
            }
        }
        return newArticle;
    }

    @Override
    public boolean deleteArticle(Article article) {
        // 将文章游记状态设置为已下架
        article.setArticleState(2);
        boolean result = this.updateById(article);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean updateArticle(Article article) {

        // 判断当前用户是否为当前文章游记的创建人
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long articleUserId = article.getUserId();
        ThrowUtils.throwIf(!loginUserId.equals(articleUserId), ErrorCode.NO_AUTH_ERROR);

        // 更新数据库
        boolean updateResult = this.updateById(article);
        ArticleDetail articleDetail = articleDetailService.getOne(new QueryWrapper<ArticleDetail>().eq("article_id", article.getId()));
        if(article.getDetail()!=null&&StringUtils.isNotBlank(article.getDetail()) && article.getDetail().length() < 8192){
            articleDetail.setDetail(article.getDetail());
        }
        boolean update = articleDetailService.updateById(articleDetail);
        //todo:加事务
        ThrowUtils.throwIf(!updateResult||!update, ErrorCode.OPERATION_ERROR);
        return true;
    }


}




