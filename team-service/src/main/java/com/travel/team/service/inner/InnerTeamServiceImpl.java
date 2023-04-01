package com.travel.team.service.inner;

import cn.hutool.core.lang.Pair;
import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.constant.CommonConstant;
import com.travel.common.model.dto.TeamQueryRequest;
import com.travel.common.model.dto.TeamDTO;
import com.travel.common.model.dto.UserDTO;
import com.travel.common.model.vo.TeamVDTO;
import com.travel.common.service.InnerTeamService;
import com.travel.common.service.InnerUserService;
import com.travel.team.mapper.TeamMapper;
import com.travel.team.model.dto.team.TeamEsDTO;
import com.travel.team.model.entity.Team;
import com.travel.team.model.vo.TeamVO;
import com.travel.team.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightCommonParameters;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;

import javax.annotation.Resource;
import java.util.*;
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

    @Resource
    private InnerUserService innerUserService;

    @Override
    public Page<TeamVDTO> searchFromEs(TeamQueryRequest teamQueryRequest) {
        // todo: 从 ES 中搜索数据，并需要从数据库中储存数据
        // ES 中只存储需要搜索的字段
        Long id = teamQueryRequest.getId();
        String searchText = teamQueryRequest.getSearchText();
        String teamName = teamQueryRequest.getTeamName();
        String intro = teamQueryRequest.getIntro();
        Long userId = teamQueryRequest.getUserId();
        Integer teamState = teamQueryRequest.getTeamState();
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

        // todo: 状态正常（需要前端控制吗）
        if (teamState != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("teamState", 0));
        }

        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("teamName", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("intro", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }

        // 按标题检索
        if (StringUtils.isNotBlank(teamName)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("teamName", teamName));
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

        // 构造查询（高亮查询）
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder)
                .withHighlightFields(new HighlightBuilder.Field("teamName")).build();

        SearchHits<TeamEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, TeamEsDTO.class);

        // 构造 Page 对象
        Page<Team> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        page.setCurrent(current);
        page.setSize(pageSize);
        List<Team> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取更多信息
        if (searchHits.hasSearchHits()) {
            List<SearchHit<TeamEsDTO>> searchHitList = searchHits.getSearchHits();

            // 构建 ES (id, teamName) 的 map
            HashMap<Long, String> teamIdNameMap = new HashMap<>();

            searchHitList.stream().forEach(searchHit ->
                    teamIdNameMap.put(searchHit.getContent().getId(), searchHit.getHighlightField("teamName").get(0)));


            // 从数据库中取出更完整的数据
            List<Team> teamList = teamMapper.selectBatchIds(teamIdNameMap.keySet());
            if (teamList != null) {
                // 将数据库中的团队列表 -> （团队 id，团队列表）
                Map<Long, List<Team>> idTeamMap = teamList.stream().collect(Collectors.groupingBy(Team::getId));

                // 遍历 ES 中的团队 id 列表，剔除数据库已经不存在的团队
                teamIdNameMap.entrySet().forEach(entry -> {
                    Long teamId = entry.getKey();
                    String highLightTeamName = entry.getValue();
                    if (idTeamMap.containsKey(teamId)) {
                        // 将 team 的非高亮字段赋值为高亮的值
                        Team team = idTeamMap.get(teamId).get(0);
                        team.setTeamName(highLightTeamName);
                        resourceList.add(team);
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(teamId), TeamEsDTO.class);
                        log.info("delete team {}", delete);
                    }
                });
            }
        }
        // 设置记录值
        page.setRecords(resourceList);

        // teamVOPage -> teamVDTOPage
        // 将 teamPage -> teamVOPage
        List<TeamVO> teamVOList = teamService.getTeamVOPage(page).getRecords();
        Page<TeamVDTO> teamVDTOPage = new Page<>();
        // 将 teamVOList -> teamVDTOList
        List<TeamVDTO> teamVDTOList = teamVOList.stream().map(team -> {
            TeamVDTO teamVDTO = new TeamVDTO();
            BeanUtils.copyProperties(team, teamVDTO);
            return teamVDTO;
        }).collect(Collectors.toList());
        // 为 teamVDTOPage 注入属性
        teamVDTOPage.setTotal(page.getTotal());
        teamVDTOPage.setRecords(teamVDTOList);
        return teamVDTOPage;

    }

}
