package com.travel.official.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.travel.common.constant.CommonConstant;
import com.travel.common.model.dto.official.OfficialQueryRequest;
import com.travel.common.model.vo.OfficialVDTO;
import com.travel.common.service.InnerOfficialService;
import com.travel.common.service.InnerUserService;
import com.travel.official.mapper.DerivativeMapper;
import com.travel.official.mapper.OfficialMapper;
import com.travel.official.model.dto.official.OfficialEsDTO;
import com.travel.official.model.entity.Derivative;
import com.travel.official.model.entity.Official;
import com.travel.official.model.vo.OfficialVO;
import com.travel.official.service.OfficialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jianping5
 * @createDate 4/4/2023 下午 6:48
 */
@Slf4j
@DubboService
public class InnerOfficialServiceImpl implements InnerOfficialService {

    @Resource
    private OfficialService officialService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private OfficialMapper officialMapper;

    @Resource
    private InnerUserService innerUserService;

    @Resource
    private Gson gson;

    @Resource
    private DerivativeMapper derivativeMapper;

    @Override
    public Page<OfficialVDTO> searchFromEs(OfficialQueryRequest officialQueryRequest) {
        // todo: 从 ES 中搜索数据，并需要从数据库中储存数据
        // ES 中只存储需要搜索的字段
        String searchText = officialQueryRequest.getSearchText();

        // es 起始页为 0
        long current = officialQueryRequest.getCurrent() - 1;
        long pageSize = officialQueryRequest.getPageSize();

        // 获取排序字段
        String sortField = officialQueryRequest.getSortField();
        String sortOrder = officialQueryRequest.getSortOrder();

        // 获取分类字段
        Integer typeId = officialQueryRequest.getTypeId();

        // 获取标签列表
        List<String> tagList = gson.fromJson(officialQueryRequest.getTag(), new TypeToken<List<String>>() {}.getType());

        // todo: 若 sortField 为 all，说明走综合排序

        // 构建 bool 查询器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 包含任何一个标签即可
        if (CollectionUtils.isNotEmpty(tagList)) {
            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String tag : tagList) {
                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tag", tag));
            }
            orTagBoolQueryBuilder.minimumShouldMatch(1);
            boolQueryBuilder.filter(orTagBoolQueryBuilder);
        }

        // 过滤（查询指定类型的官方）
        if (typeId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("typeId", typeId));
        }

        // 按省份 + 城市检索
        String province = officialQueryRequest.getProvince();
        String city = officialQueryRequest.getCity();
        if (!StringUtils.isAnyBlank(province, city)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("province", province));
            boolQueryBuilder.filter(QueryBuilders.termQuery("city", city));
        }

        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("officialName", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("intro", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("province", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("city", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("location", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }

        // 排序
        /*SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }*/

        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);

        // 构造查询（高亮查询）
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .withHighlightFields(new HighlightBuilder.Field("officialName")).build();

        SearchHits<OfficialEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, OfficialEsDTO.class);

        // 构造 Page 对象
        Page<Official> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        page.setCurrent(current);
        page.setSize(pageSize);
        List<Official> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取更多信息
        if (searchHits.hasSearchHits()) {
            List<SearchHit<OfficialEsDTO>> searchHitList = searchHits.getSearchHits();

            // 构建 ES (id, officialName) 的 map
            HashMap<Long, String> officialIdNameMap = new HashMap<>();

            searchHitList.stream().forEach(searchHit ->
                    officialIdNameMap.put(searchHit.getContent().getId(), searchHit.getHighlightField("officialName").get(0)));

            // todo: 若 sortField 为 all，说明走综合排序
            // 从数据库中取出更完整的数据（并排序）
            QueryWrapper<Official> officialQueryWrapper = new QueryWrapper<>();
            officialQueryWrapper.in("id", officialIdNameMap.keySet());
            if ("all".equals(sortField)) {
                officialQueryWrapper.last("order by 5*like_count+3*favorite_count+view_count+review_count desc");
            } else {
                officialQueryWrapper.orderBy(true, CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
            }
            List<Official> officialList = officialMapper.selectList(officialQueryWrapper);

            if (officialList != null) {
                // 将数据库中的团队列表 -> （团队 id，团队列表）
                Map<Long, List<Official>> idOfficialMap = officialList.stream().collect(Collectors.groupingBy(Official::getId));

                // 遍历 ES 中的团队 id 列表，剔除数据库已经不存在的团队
                officialIdNameMap.entrySet().forEach(entry -> {
                    Long officialId = entry.getKey();
                    String highLightOfficialName = entry.getValue();
                    if (idOfficialMap.containsKey(officialId)) {
                        // 将 team 的非高亮字段赋值为高亮的值
                        Official official = idOfficialMap.get(officialId).get(0);
                        official.setOfficialName(highLightOfficialName);
                        resourceList.add(official);
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(officialId), OfficialEsDTO.class);
                        log.info("delete official {}", delete);
                    }
                });
            }
        }
        // 设置记录值
        page.setRecords(resourceList);

        return objPageToVdtoPage(page);
    }

    @Override
    public Page<OfficialVDTO> searchRcmdFromEs(OfficialQueryRequest officialQueryRequest) {
        // es 起始页为 0
        long current = officialQueryRequest.getCurrent() - 1;
        long pageSize = officialQueryRequest.getPageSize();

        // 获取分类字段
        Integer typeId = officialQueryRequest.getTypeId();

        // 获取经纬度
        String latAndLong = officialQueryRequest.getLatAndLong();

        // 获取排序字段
        String sortField = officialQueryRequest.getSortField();
        String sortOrder = officialQueryRequest.getSortOrder();

        // 构建 bool 查询器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 过滤（查询指定类型的官方）
        if (typeId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("typeId", typeId));
        }

        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);

        // 按经纬度排序
        GeoDistanceSortBuilder sortBuilder = SortBuilders.geoDistanceSort("location", new GeoPoint(latAndLong))
                .order(SortOrder.ASC)
                .unit(DistanceUnit.KILOMETERS);

        // todo：待测试
        // 构造查询（经纬度排序查询）
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .withSorts(sortBuilder)
                .build();

        SearchHits<OfficialEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, OfficialEsDTO.class);

        // 构造 Page 对象
        Page<Official> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        page.setCurrent(current);
        page.setSize(pageSize);
        List<Official> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取更多信息
        if (searchHits.hasSearchHits()) {
            List<SearchHit<OfficialEsDTO>> searchHitList = searchHits.getSearchHits();

            // 构建 ES id 的 Set
            Set<Long> idSet = searchHitList.stream().map(searchHit -> searchHit.getContent().getId()).collect(Collectors.toSet());

            // todo: 若 sortField 为 all，说明走综合排序
            // 从数据库中取出更完整的数据（并排序）
            QueryWrapper<Official> officialQueryWrapper = new QueryWrapper<>();
            officialQueryWrapper.in("id", idSet);
            officialQueryWrapper.orderBy(true, CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
            List<Official> officialList = officialMapper.selectList(officialQueryWrapper);

            if (officialList != null) {
                // 将数据库中的官方列表 -> （官方 id，官方列表）
                Map<Long, List<Official>> idOfficialMap = officialList.stream().collect(Collectors.groupingBy(Official::getId));

                // 遍历 ES 中的官方 id 列表，剔除数据库已经不存在的官方
                idSet.forEach(officialId -> {
                    if (idOfficialMap.containsKey(officialId)) {
                        Official official = idOfficialMap.get(officialId).get(0);
                        resourceList.add(official);
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(officialId), OfficialEsDTO.class);
                        log.info("delete official {}", delete);
                    }
                });
            }
        }
        // 设置记录值
        page.setRecords(resourceList);

        return objPageToVdtoPage(page);
    }

    @Override
    public List<Long> listDerivativeId(Long userId) {
        QueryWrapper<Derivative> derivativeQueryWrapper = new QueryWrapper<>();
        derivativeQueryWrapper.eq("user_id", userId);
        derivativeQueryWrapper.select("id");
        List<Long> derivativeIdList = derivativeMapper.selectList(derivativeQueryWrapper).stream().map(derivative -> derivative.getId()).collect(Collectors.toList());

        return derivativeIdList;
    }

    @Override
    public Page<OfficialVDTO> listPersonalRcmd(Set<Long> idList, long pageNum, long pageSize) {
        // todo：查询个性化推荐实体列表
        // 先根据行为对象 id 列表查询对应的省份集
        QueryWrapper<Official> officialQueryWrapper = new QueryWrapper<>();
        officialQueryWrapper.select("province");
        officialQueryWrapper.in("id", idList);
        // 类型id（景区）
        officialQueryWrapper.eq("type_id", 1);
        Set<String> provinceSet = officialService.list(officialQueryWrapper).stream().map(official -> official.getProvince()).collect(Collectors.toSet());

        // 根据省份集查询这些用户最近发布的游记
        QueryWrapper<Official> newOfficialQueryWrapper = new QueryWrapper<>();
        newOfficialQueryWrapper.in("province", provinceSet);
        Page<Official> page = officialService.page(new Page<>(pageNum, pageSize), newOfficialQueryWrapper);

        return objPageToVdtoPage(page);
    }

    /**
     * 将实体 page 转换成 VDTO page
     * @param officialPage
     * @return
     */
    private Page<OfficialVDTO> objPageToVdtoPage(Page<Official> officialPage) {
        // officialPage -> officialVDTOPage
        // 将 officialPage -> officialVOPage -> officialVOList
        List<OfficialVO> officialVOList = officialService.getOfficialVOPage(officialPage).getRecords();

        Page<OfficialVDTO> officialVDTOPage = new Page<>(officialPage.getCurrent(), officialPage.getSize());

        // 将 officialVOList -> officialVDTOList
        List<OfficialVDTO> officialVDTOList = officialVOList.stream().map(officialVO -> {
            OfficialVDTO officialVDTO = new OfficialVDTO();
            BeanUtils.copyProperties(officialVO, officialVDTO);
            return officialVDTO;
        }).collect(Collectors.toList());

        // 为 officialVDTOPage 注入属性
        officialVDTOPage.setTotal(officialPage.getTotal());
        officialVDTOPage.setRecords(officialVDTOList);

        return officialVDTOPage;
    }
}
