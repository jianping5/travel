package com.travel.data.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.TypeConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.TeamQueryRequest;
import com.travel.common.model.vo.SearchVDTO;
import com.travel.common.model.vo.TeamVDTO;
import com.travel.data.datasource.DataSource;
import com.travel.data.datasource.DataSourceRegistry;
import com.travel.data.datasource.TeamDataSource;
import com.travel.data.model.dto.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索门面
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private TeamDataSource teamDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public SearchVDTO searchAll(@RequestBody SearchRequest searchRequest) {
        // 获取请求类型
        String type = searchRequest.getType();
        TypeConstant typeConstant = TypeConstant.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);

        // 获取请求关键词，当前页码，页大小
        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        // todo: 搜索出所有数据
        if (typeConstant == null) {

            CompletableFuture<Page<TeamVDTO>> teamTask = CompletableFuture.supplyAsync(() -> {
                TeamQueryRequest teamQueryRequest = new TeamQueryRequest();
                teamQueryRequest.setSearchText(searchText);
                Page<TeamVDTO> teamVDTOPage = teamDataSource.doSearch(searchText, current, pageSize);
                return teamVDTOPage;
            });

            CompletableFuture.allOf(teamTask).join();

            // todo：填充数据
            try {
                Page<TeamVDTO> teamVDTOPage = teamTask.get();
                SearchVDTO searchVDTO = new SearchVDTO();
                searchVDTO.setTeamVDTOPage(teamVDTOPage);
                return searchVDTO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            // todo：获取指定类型的数据
            SearchVDTO searchVDTO = new SearchVDTO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVDTO.setDataPage(page);
            return searchVDTO;
        }
    }
}
