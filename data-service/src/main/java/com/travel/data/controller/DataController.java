package com.travel.data.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.model.dto.travel.TravelRcmdRequest;
import com.travel.common.model.vo.OfficialVDTO;
import com.travel.common.model.vo.SearchVDTO;
import com.travel.common.service.InnerTravelService;
import com.travel.data.datasource.OfficialDataSource;
import com.travel.data.model.dto.OfficialRcmdRequest;
import com.travel.data.model.dto.PersonalRcmdRequest;
import com.travel.data.service.BehaviorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 11/4/2023 下午 8:19
 */
@RestController
@Api(tags = "数据 Controller")
public class DataController {

    @Resource
    private BehaviorService behaviorService;

    @Resource
    private OfficialDataSource officialDataSource;

    @Resource
    private InnerTravelService innerTravelService;

    /**
     * 分页获取个性化推荐
     * @param personalRcmdRequest
     * @return
     */
    @ApiModelProperty(value = "分页获取个性化推荐")
    @PostMapping("/personal/rcmd")
    private BaseResponse<SearchVDTO> listPersonalRcmd(@RequestBody PersonalRcmdRequest personalRcmdRequest) {
        if (personalRcmdRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(behaviorService.listPersonalRcmd(personalRcmdRequest));
    }

    /**
     * 分页获取景区推荐酒店/美食
     * @param officialRcmdRequest
     * @return
     */
    @ApiModelProperty(value = "分页获取景区推荐酒店/美食")
    @PostMapping("/official/rcmd")
    private BaseResponse<Page<OfficialVDTO>> listOfficialRcmd(@RequestBody OfficialRcmdRequest officialRcmdRequest) {
        if (officialRcmdRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(officialDataSource.doRcmd(officialRcmdRequest));
    }

    /**
     * 分页获取游记推荐
     * @param travelRcmdRequest
     * @return
     */
    @ApiModelProperty(value = "分页获取游记推荐")
    @PostMapping("/travel/rcmd")
    private BaseResponse<SearchVDTO> listTravelRcmd(@RequestBody TravelRcmdRequest travelRcmdRequest) {
        if (travelRcmdRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        SearchVDTO searchVDTO = innerTravelService.listTravelRcmd(travelRcmdRequest);

        // todo：游记
        return ResultUtils.success(searchVDTO);
    }

}
