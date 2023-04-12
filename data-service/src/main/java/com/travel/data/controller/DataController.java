package com.travel.data.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.model.vo.OfficialVDTO;
import com.travel.common.model.vo.SearchVDTO;
import com.travel.data.datasource.OfficialDataSource;
import com.travel.data.model.dto.OfficialRcmdRequest;
import com.travel.data.model.dto.PersonalRcmdRequest;
import com.travel.data.service.BehaviorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 11/4/2023 下午 8:19
 */
@RestController
@RequestMapping("/data")
public class DataController {

    @Resource
    private BehaviorService behaviorService;

    @Resource
    private OfficialDataSource officialDataSource;

    @PostMapping("/personal/rcmd")
    private BaseResponse<SearchVDTO> listPersonalRcmd(@RequestBody PersonalRcmdRequest personalRcmdRequest) {
        if (personalRcmdRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(behaviorService.listPersonalRcmd(personalRcmdRequest));
    }

    @PostMapping("/official/rcmd")
    private BaseResponse<Page<OfficialVDTO>> listOfficialRcmd(@RequestBody OfficialRcmdRequest officialRcmdRequest) {
        if (officialRcmdRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(officialDataSource.doRcmd(officialRcmdRequest));
    }
}
