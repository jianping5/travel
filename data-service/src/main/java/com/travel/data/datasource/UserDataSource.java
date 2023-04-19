package com.travel.data.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.user.UserQueryRequest;
import com.travel.common.model.vo.UserVDTO;
import com.travel.common.service.InnerUserService;
import com.travel.data.model.dto.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @author jianping5
 * @createDate 12/4/2023 下午 3:08
 */
@Service
@Slf4j
public class UserDataSource implements DataSource<UserVDTO> {


    @DubboReference
    private InnerUserService innerUserService;

    @Override
    public Page<UserVDTO> doSearch(SearchRequest searchRequest, long pageNum, long pageSize) {
        // 获取用户查询请求
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setSearchText(searchRequest.getSearchText());
        userQueryRequest.setCurrent(pageNum);
        userQueryRequest.setPageSize(pageSize);
        userQueryRequest.setSortField(searchRequest.getSortField());
        userQueryRequest.setSortOrder(searchRequest.getSortOrder());

        return innerUserService.searchFromEs(userQueryRequest);
    }
}
