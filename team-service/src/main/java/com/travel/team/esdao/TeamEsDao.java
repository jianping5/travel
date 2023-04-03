package com.travel.team.esdao;

import com.travel.team.model.dto.team.TeamEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author jianping5
 * @createDate 31/3/2023 下午 2:47
 */
public interface TeamEsDao extends ElasticsearchRepository<TeamEsDTO, Long> {
}
