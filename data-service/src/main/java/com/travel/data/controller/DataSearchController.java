package com.travel.data.controller;


import com.travel.common.common.BaseResponse;
import com.travel.common.common.ResultUtils;
import com.travel.common.model.vo.SearchVDTO;
import com.travel.data.manager.SearchFacade;
import com.travel.data.model.dto.SearchRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 图片接口
 *
 * @author jianping5
 */
@RestController
@RequestMapping("/search")
@Api(tags = "数据搜索 Controller")
public class DataSearchController {

    @Resource
    private SearchFacade searchFacade;

    /**
     * 搜索
     * @param searchRequest
     * @return
     */
    @ApiModelProperty(value = "搜索")
    @PostMapping("/all")
    public BaseResponse<SearchVDTO> searchAll(@RequestBody SearchRequest searchRequest) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest));
    }

}
