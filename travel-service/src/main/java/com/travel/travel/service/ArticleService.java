package com.travel.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.travel.model.vo.ArticleVO;
import com.travel.travel.model.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.travel.model.entity.ArticleDetail;
import com.travel.travel.model.request.ArticleQueryRequest;

/**
* @author jianping5
* @description 针对表【article(文章表)】的数据库操作Service
* @createDate 2023-03-24 19:23:06
*/
public interface ArticleService extends IService<Article> {

    /**
     * 校验 Article
     * @param article
     * @param b
     */
    void validArticle(Article article, boolean b);

    /**
     * 获取文章游记视图体
     * @param Article
     * @return
     */
    ArticleVO getArticleVO(Article Article, ArticleDetail articleDetail);

    /**
     * 获取分页文章游记视图体
     * @param ArticlePage
     * @return
     */
    Page<ArticleVO> getArticleVOPage(Page<Article> ArticlePage);

    /**
     * 根据请求体获取请求 Wrapper
     */
    Page<Article> queryArticle(ArticleQueryRequest articleQueryRequest);

    /**
     * 创建文章游记
     * @param Article
     * @return
     */
    Article addArticle(Article Article);


    /**
     * 解散文章游记
     * @param Article
     * @return
     */
    boolean deleteArticle(Article Article);

    /**
     * 更新文章游记
     * @param Article
     * @return
     */
    boolean updateArticle(Article Article);


}
