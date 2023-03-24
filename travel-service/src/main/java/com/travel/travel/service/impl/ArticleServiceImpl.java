package com.travel.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.travel.model.entity.Article;
import com.travel.travel.service.ArticleService;
import com.travel.travel.mapper.ArticleMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【article(文章表)】的数据库操作Service实现
* @createDate 2023-03-24 19:23:06
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

}




