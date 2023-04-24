package com.travel.travel.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.vo.ArticleVDTO;
import com.travel.common.service.InnerArticleService;
import com.travel.travel.model.entity.Article;
import com.travel.travel.model.vo.ArticleVO;
import com.travel.travel.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 6:33
 */
@DubboService
@Slf4j
public class InnerArticleServiceImpl implements InnerArticleService {

    @Resource
    private ArticleService articleService;

    @Override
    public Page<ArticleVDTO> listPersonalRcmd(Set<Long> idList, long pageNum, long pageSize) {
        // todo：查询个性化推荐实体列表
        // 先根据行为对象 id 列表查询对应的用户集
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.select("user_id");
        articleQueryWrapper.in(CollectionUtils.isNotEmpty(idList), "id", idList);
        Set<Long> userIdSet = articleService.list(articleQueryWrapper).stream().map(article -> article.getUserId()).collect(Collectors.toSet());

        // 根据用户集查询这些用户最近发布的游记
        QueryWrapper<Article> newArticleQueryWrapper = new QueryWrapper<>();
        newArticleQueryWrapper.in(CollectionUtils.isNotEmpty(userIdSet), "user_id", userIdSet);
        newArticleQueryWrapper.orderByDesc("create_time");
        Page<Article> page = articleService.page(new Page<>(pageNum, pageSize), newArticleQueryWrapper);

        return articleObjPageToVdtoPage(page);
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
}
