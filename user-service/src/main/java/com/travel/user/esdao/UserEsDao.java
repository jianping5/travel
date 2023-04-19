package com.travel.user.esdao;

import com.travel.user.model.dto.UserEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author jianping5
 * @createDate 31/3/2023 下午 2:47
 */
public interface UserEsDao extends ElasticsearchRepository<UserEsDTO, Long> {
}
