package com.travel.travel.mapper;

import com.travel.travel.model.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
* @author jianping5
* @description 针对表【article(文章表)】的数据库操作Mapper
* @createDate 2023-03-24 19:23:06
* @Entity com.travel.travel.model.entity.Article
*/
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 查询某段时间的文章列表（包括已被删除的数据）
     * @param minUpdateTime
     * @return
     */
    List<Article> listArticleWithDelete(Date minUpdateTime);
}




