package com.travel.data.datasource;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.travel.VideoQueryRequest;
import com.travel.common.model.vo.VideoVDTO;
import com.travel.common.service.InnerTravelService;
import com.travel.data.model.dto.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @author jianping5
 * @createDate 4/4/2023 下午 6:44
 */
@Service
@Slf4j
public class VideoDataSource implements DataSource<VideoVDTO> {

    @DubboReference
    private InnerTravelService innerTravelService;

    @Override
    public Page<VideoVDTO> doSearch(SearchRequest searchRequest, long pageNum, long pageSize) {
        // 获取视频查询请求
        VideoQueryRequest videoQueryRequest = new VideoQueryRequest();
        videoQueryRequest.setSearchText(searchRequest.getSearchText());
        videoQueryRequest.setCurrent(pageNum);
        videoQueryRequest.setPageSize(pageSize);
        videoQueryRequest.setSortField(searchRequest.getSortField());
        videoQueryRequest.setSortOrder(searchRequest.getSortOrder());

        return innerTravelService.searchFromEs(videoQueryRequest);
    }

/*    *//**
     * todo：获取景区推荐酒店/美食
     * @param officialRcmdRequest
     * @return
     *//*
    public Page<OfficialVDTO> doRcmd(OfficialRcmdRequest officialRcmdRequest) {
        // 获取官方推荐请求
        OfficialQueryRequest officialQueryRequest = new OfficialQueryRequest();
        officialQueryRequest.setId(officialRcmdRequest.getOfficialId());
        officialQueryRequest.setTypeId(officialRcmdRequest.getRcmdType());
        officialQueryRequest.setCurrent(officialRcmdRequest.getCurrent());
        officialQueryRequest.setPageSize(officialRcmdRequest.getPageSize());
        officialQueryRequest.setLatAndLong(officialRcmdRequest.getLatAndLong());
        officialQueryRequest.setSortField(officialRcmdRequest.getSortField());
        officialQueryRequest.setSortOrder(officialRcmdRequest.getSortOrder());

        return innerOfficialService.searchRcmdFromEs(officialQueryRequest);
    }*/
}
