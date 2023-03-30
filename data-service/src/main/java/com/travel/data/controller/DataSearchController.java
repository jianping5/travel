package com.travel.data.controller;


import com.travel.common.common.BaseResponse;
import com.travel.common.common.ResultUtils;
import com.travel.common.model.vo.SearchVDTO;
import com.travel.data.manager.SearchFacade;
import com.travel.data.model.dto.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 图片接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/data-search")
@Slf4j
public class DataSearchController {

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVDTO> searchAll(@RequestBody SearchRequest searchRequest) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest));
    }

}
