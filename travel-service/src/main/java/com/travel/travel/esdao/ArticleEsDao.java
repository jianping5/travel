package com.travel.travel.esdao;

import com.travel.travel.model.dto.ArticleEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author jianping5
 * @createDate 31/3/2023 下午 2:47
 */
public interface ArticleEsDao extends ElasticsearchRepository<ArticleEsDTO, Long> {
}
