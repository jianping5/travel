package com.travel.official.esdao;

import com.travel.official.model.dto.derivative.DerivativeEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author jianping5
 * @createDate 31/3/2023 下午 2:47
 */
public interface DerivativeEsDao extends ElasticsearchRepository<DerivativeEsDTO, Long> {
}
