package com.travel.travel.job.once;

import com.travel.travel.esdao.ArticleEsDao;
import com.travel.travel.model.dto.ArticleEsDTO;
import com.travel.travel.model.entity.Article;
import com.travel.travel.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步团队到 es
 *
 * @author jianping5
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class FullSyncArticleToEs implements CommandLineRunner {

    @Resource
    private ArticleService articleService;

    @Resource
    private ArticleEsDao articleEsDao;

    @Override
    public void run(String... args) {
        List<Article> articleList = articleService.list();
        if (CollectionUtils.isEmpty(articleList)) {
            return;
        }
        List<ArticleEsDTO> articleEsDTOList = articleList.stream().map(ArticleEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = articleEsDTOList.size();
        log.info("FullSyncArticleToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            articleEsDao.saveAll(articleEsDTOList.subList(i, end));
        }
        log.info("FullSyncArticleToEs end, total {}", total);
    }
}
