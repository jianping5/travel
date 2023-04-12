package com.travel.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.UserHolder;
import com.travel.travel.model.vo.ArticleVO;
import com.travel.travel.model.entity.Article;
import com.travel.travel.model.entity.ArticleDetail;
import com.travel.travel.model.request.ArticleQueryRequest;
import com.travel.travel.service.ArticleDetailService;
import com.travel.travel.service.ArticleService;
import com.travel.travel.mapper.ArticleMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【article(文章表)】的数据库操作Service实现
* @createDate 2023-03-24 19:23:06
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{
    @DubboReference
    private InnerUserService innerUserService;
    @Resource
    private ArticleDetailService articleDetailService;
    @Override
    public void validArticle(Article article, boolean add) {
        if (article == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = article.getUserId();
        String intro = article.getIntro();
        String coverUrl = article.getCoverUrl();
        Integer permission = article.getPermission();
        String tag = article.getTag();
        String location = article.getLocation();
        String detail = article.getDetail();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(coverUrl,location,detail,tag,intro), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.anyNull(userId,intro,coverUrl,permission,tag,location,detail),ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(detail) && detail.length() > 8192) {
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

        // 填充信息
        List<ArticleVO> articleVOList = articleList.stream().map(article -> {
            ArticleDetail articleDetail = articleDetailService.getOne(new QueryWrapper<ArticleDetail>().eq("article_id", article.getId()));
            if (articleDetail==null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            ArticleVO articleVO = ArticleVO.objToVo(article,articleDetail);
            return articleVO;
        }).collect(Collectors.toList());
        articleVOPage.setRecords(articleVOList);
        return articleVOPage;
    }

    @Override
    public Page<Article> queryArticle(ArticleQueryRequest articleQueryRequest) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        if (articleQueryRequest == null) {
            return null;
        }
        // 获取查询条件中的字段
        String searchText = articleQueryRequest.getSearchText();
        Long userId = articleQueryRequest.getUserId();
        Integer permission = articleQueryRequest.getPermission();
        Long teamId = articleQueryRequest.getTeamId();
        Integer queryType = articleQueryRequest.getQueryType();
        Integer articleState = articleQueryRequest.getArticleState();
        Long pageSize = articleQueryRequest.getPageSize();
        Long current = articleQueryRequest.getCurrent();
        Integer orderType = articleQueryRequest.getOrderType();
        int fromIndex = 0;
        //内容模糊限制
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("tag", searchText).or().like("intro", searchText).or().like("title", searchText);
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
            queryWrapper.last("order by 0.3*like_count + 0.3 * comment_comment + 0.3*favorite_count + 0.1*view_count desc");

        }else if(orderType!=null&&orderType.equals(1)){
            //最新发布
            queryWrapper.orderByDesc("update_time");

        }

        // todo:猜你喜欢直接走数据服务接口
//        Page<Article> articlePage = new Page<>();
//        List<Article> articles = list(queryWrapper);
//        //起始位置限制
//        if(lastEndId != null&&articles!=null){
//            fromIndex = Math.min(articles.indexOf(getById(lastEndId)),articles.size());
//            articlePage.setTotal(articles.size());
//        }
//        List<Article> articleList = list(queryWrapper.select("*").last("limit " + pageSize + " offset " + fromIndex));
//        articlePage.setSize(pageSize);
//        articlePage.setRecords(articleList);
//        articlePage.setCurrent(0);
        Page page = page(new Page(current, pageSize), queryWrapper);
        return page;
    }

    @Override
    public Article addArticle(Article article) {
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        // 设置用户 id
        article.setUserId(loginUserId);

        //todo:考虑事务
        // 添加到数据库中
        boolean saveResult = this.save(article);
        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setDetail(article.getDetail());
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
        if(article.getDetail()!=null&&StringUtils.isNotBlank(article.getDetail()) && article.getDetail().length() > 8192){
            articleDetail.setDetail(article.getDetail());
        }
        boolean update = articleDetailService.updateById(articleDetail);
        //todo:加事务
        ThrowUtils.throwIf(!updateResult||!update, ErrorCode.OPERATION_ERROR);
        return true;
    }


}




