package com.travel.official.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.constant.CommonConstant;
import com.travel.common.model.dto.official.DerivativeQueryRequest;
import com.travel.common.model.vo.DerivativeVDTO;
import com.travel.common.service.InnerDerivativeService;
import com.travel.common.service.InnerUserService;
import com.travel.official.mapper.DerivativeMapper;
import com.travel.official.model.dto.derivative.DerivativeEsDTO;
import com.travel.official.model.entity.Derivative;
import com.travel.official.model.vo.DerivativeVO;
import com.travel.official.service.DerivativeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 周边内部服务
 * @author jianping5
 */
@Slf4j
@DubboService
public class InnerDerivativeServiceImpl implements InnerDerivativeService {

    @Resource
    private DerivativeService derivativeService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private DerivativeMapper derivativeMapper;

    @Resource
    private InnerUserService innerUserService;

    @Override
    public Page<DerivativeVDTO> searchFromEs(DerivativeQueryRequest derivativeQueryRequest) {
        // todo: 从 ES 中搜索数据，并需要从数据库中储存数据
        // ES 中只存储需要搜索的字段
        String searchText = derivativeQueryRequest.getSearchText();

        // es 起始页为 0
        long current = derivativeQueryRequest.getCurrent() - 1;
        long pageSize = derivativeQueryRequest.getPageSize();

        // 获取排序字段
        String sortField = derivativeQueryRequest.getSortField();
        String sortOrder = derivativeQueryRequest.getSortOrder();

        // 构建 bool 查询器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 状态正常的
        boolQueryBuilder.filter(QueryBuilders.termQuery("derivativeState", 0));

        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("derivativeName", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("intro", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }

        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);

        // 构造查询（高亮查询）
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .withHighlightFields(new HighlightBuilder.Field("derivativeName")).build();

        SearchHits<DerivativeEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, DerivativeEsDTO.class);

        // 构造 Page 对象
        Page<Derivative> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        page.setCurrent(current);
        page.setSize(pageSize);
        List<Derivative> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取更多信息
        if (searchHits.hasSearchHits()) {
            List<SearchHit<DerivativeEsDTO>> searchHitList = searchHits.getSearchHits();

            // 构建 ES (id, derivativeName) 的 map
            HashMap<Long, String> derivativeIdNameMap = new HashMap<>();

            searchHitList.stream().forEach(searchHit ->
                    derivativeIdNameMap.put(searchHit.getContent().getId(), searchHit.getHighlightField("derivativeName").get(0)));

            // todo: 若 sortField 为 all，说明走综合排序
            // 从数据库中取出更完整的数据（并排序）
            QueryWrapper<Derivative> derivativeQueryWrapper = new QueryWrapper<>();
            derivativeQueryWrapper.in("id", derivativeIdNameMap.keySet());
            derivativeQueryWrapper.orderBy(true, CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
            List<Derivative> derivativeList = derivativeMapper.selectList(derivativeQueryWrapper);

            if (derivativeList != null) {
                // 将数据库中的周边列表 -> （周边 id，周边列表）
                Map<Long, List<Derivative>> idDerivativeMap = derivativeList.stream().collect(Collectors.groupingBy(Derivative::getId));

                // 遍历 ES 中的周边 id 列表，剔除数据库已经不存在的周边
                derivativeIdNameMap.entrySet().forEach(entry -> {
                    Long derivativeId = entry.getKey();
                    String highLightDerivativeName = entry.getValue();
                    if (idDerivativeMap.containsKey(derivativeId)) {
                        // 将 derivative 的非高亮字段赋值为高亮的值
                        Derivative derivative = idDerivativeMap.get(derivativeId).get(0);
                        derivative.setDerivativeName(highLightDerivativeName);
                        resourceList.add(derivative);
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(derivativeId), DerivativeEsDTO.class);
                        log.info("delete derivative {}", delete);
                    }
                });
            }
        }
        // 设置记录值
        page.setRecords(resourceList);

        // derivativePage -> derivativeVDTOPage
        // 将 derivativePage -> derivativeVOPage -> derivativeVOList
        List<DerivativeVO> derivativeVOList = derivativeService.getDerivativeVOPage(page).getRecords();

        Page<DerivativeVDTO> derivativeVDTOPage = new Page<>();

        // 将 derivativeVOList -> derivativeVDTOList
        List<DerivativeVDTO> derivativeVDTOList = derivativeVOList.stream().map(derivativeVO -> {
            DerivativeVDTO derivativeVDTO = new DerivativeVDTO();
            BeanUtils.copyProperties(derivativeVO, derivativeVDTO);
            return derivativeVDTO;
        }).collect(Collectors.toList());

        // 为 derivativeVDTOPage 注入属性
        derivativeVDTOPage.setTotal(page.getTotal());
        derivativeVDTOPage.setRecords(derivativeVDTOList);

        return derivativeVDTOPage;
    }
}
