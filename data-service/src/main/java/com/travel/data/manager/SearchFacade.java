package com.travel.data.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.TypeConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.vo.*;
import com.travel.data.datasource.*;
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
    private OfficialDataSource officialDataSource;

    @Resource
    private DerivativeDataSource derivativeDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private ArticleDataSource articleDataSource;

    @Resource
    private VideoDataSource videoDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public SearchVDTO searchAll(@RequestBody SearchRequest searchRequest) {
        // 获取请求类型
        String type = searchRequest.getType();
        TypeConstant typeConstant = TypeConstant.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);

        // 获取请求关键词，当前页码，页大小
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        // todo: 搜索出所有数据
        if (typeConstant == null) {

            CompletableFuture<Page<TeamVDTO>> teamTask = CompletableFuture.supplyAsync(() -> {
                Page<TeamVDTO> teamVDTOPage = teamDataSource.doSearch(searchRequest, current, pageSize);
                return teamVDTOPage;
            });

            CompletableFuture<Page<OfficialVDTO>> officialTask = CompletableFuture.supplyAsync(() -> {
                Page<OfficialVDTO> officialVDTOPage = officialDataSource.doSearch(searchRequest, current, pageSize);
                return officialVDTOPage;
            });

            CompletableFuture<Page<DerivativeVDTO>> derivativeTask = CompletableFuture.supplyAsync(() -> {
                Page<DerivativeVDTO> derivativeVDTOPage = derivativeDataSource.doSearch(searchRequest, current, pageSize);
                return derivativeVDTOPage;
            });

            CompletableFuture<Page<UserVDTO>> userTask = CompletableFuture.supplyAsync(() -> {
                Page<UserVDTO> userVDTOPage = userDataSource.doSearch(searchRequest, current, pageSize);
                return userVDTOPage;
            });

            CompletableFuture<Page<ArticleVDTO>> articleTask = CompletableFuture.supplyAsync(() -> {
                Page<ArticleVDTO> articleVDTOPage = articleDataSource.doSearch(searchRequest, current, pageSize);
                return articleVDTOPage;
            });

            CompletableFuture<Page<VideoVDTO>> videoTask = CompletableFuture.supplyAsync(() -> {
                Page<VideoVDTO> videoVDTOPage = videoDataSource.doSearch(searchRequest, current, pageSize);
                return videoVDTOPage;
            });

            CompletableFuture.allOf(teamTask, officialTask, derivativeTask, userTask, articleTask, videoTask).join();

            // todo：填充数据
            try {
                // 获取团队
                Page<TeamVDTO> teamVDTOPage = teamTask.get();

                // 获取官方
                Page<OfficialVDTO> officialVDTOPage = officialTask.get();

                // 获取周边
                Page<DerivativeVDTO> derivativeVDTOPage = derivativeTask.get();

                // 获取用户
                Page<UserVDTO> userVDTOPage = userTask.get();

                // 获取文章游记
                Page<ArticleVDTO> articleVDTOPage = articleTask.get();

                // 获取视频游记
                Page<VideoVDTO> videoVDTOPage = videoTask.get();

                SearchVDTO searchVDTO = new SearchVDTO();
                searchVDTO.setTeamVDTOPage(teamVDTOPage);
                searchVDTO.setOfficialVDTOPage(officialVDTOPage);
                searchVDTO.setDerivativeVDTOPage(derivativeVDTOPage);
                searchVDTO.setUserVDTOPage(userVDTOPage);
                searchVDTO.setArticleVDTOPage(articleVDTOPage);
                searchVDTO.setVideoVDTOPage(videoVDTOPage);

                return searchVDTO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            // todo：获取指定类型的数据
            SearchVDTO searchVDTO = new SearchVDTO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchRequest, current, pageSize);
            searchVDTO.setDataPage(page);
            return searchVDTO;
        }
    }
}
