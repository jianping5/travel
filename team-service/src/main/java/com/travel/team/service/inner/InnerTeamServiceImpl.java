package com.travel.team.service.inner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.constant.CommonConstant;
import com.travel.common.model.dto.TeamQueryRequest;
import com.travel.common.model.dto.TeamDTO;
import com.travel.common.model.es.TeamEsDTO;
import com.travel.common.model.vo.TeamVDTO;
import com.travel.common.service.InnerTeamService;
import com.travel.team.mapper.TeamMapper;
import com.travel.team.model.entity.Team;
import com.travel.team.model.vo.TeamVO;
import com.travel.team.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 8:27
 */
@Slf4j
@DubboService
public class InnerTeamServiceImpl implements InnerTeamService {

    @Resource
    private TeamService teamService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private TeamMapper teamMapper;


    @Override
    public String sayHello() {
        return "Hello";
    }


    @Override
    public Page<TeamDTO> searchFromEs(TeamQueryRequest teamQueryRequest) {
        // todo: 从 ES 中搜索数据，并需要从数据库中储存数据
        // ES 中只存储需要搜索的字段
        Long id = teamQueryRequest.getId();
        String searchText = teamQueryRequest.getSearchText();
        String teamName = teamQueryRequest.getTeamName();
        String intro = teamQueryRequest.getIntro();
        Long userId = teamQueryRequest.getUserId();
        // es 起始页为 0
        long current = teamQueryRequest.getCurrent() - 1;
        long pageSize = teamQueryRequest.getPageSize();
        String sortField = teamQueryRequest.getSortField();
        String sortOrder = teamQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }

        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("team_name", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("intro", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(teamName)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("team_name", teamName));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按内容检索
        if (StringUtils.isNotBlank(intro)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("intro", intro));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder).build();
        SearchHits<TeamEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, TeamEsDTO.class);
        Page<Team> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<Team> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<TeamEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> teamIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());
            // 从数据库中取出更完整的数据
            List<Team> teamList = teamMapper.selectBatchIds(teamIdList);
            if (teamList != null) {
                Map<Long, List<Team>> idPostMap = teamList.stream().collect(Collectors.groupingBy(Team::getId));
                teamIdList.forEach(teamId -> {
                    if (idPostMap.containsKey(teamId)) {
                        resourceList.add(idPostMap.get(teamId).get(0));
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(teamId), TeamEsDTO.class);
                        log.info("delete post {}", delete);
                    }
                });
            }
        }
        page.setRecords(resourceList);
        Page<TeamDTO> teamDTOPage = new Page<>();
        BeanUtils.copyProperties(page, teamDTOPage);

        return teamDTOPage;
    }

    @Override
    public Page<TeamVDTO> getTeamVOPage(Page<TeamDTO> teamDTOPage) {
        // todo: 根据 team 页获取 teamVO 页
        // todo：涉及 teamDTOPage -> teamPage，teamVOPage -> teamVDTOPage
        Page<Team> teamPage = new Page<>();
        BeanUtils.copyProperties(teamDTOPage, teamPage);

        Page<TeamVO> teamVOPage = teamService.getTeamVOPage(teamPage);

        Page<TeamVDTO> teamVDTOPage = new Page<>();
        BeanUtils.copyProperties(teamVOPage, teamVDTOPage);

        return teamVDTOPage;
    }

}
