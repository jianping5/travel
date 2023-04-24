package com.travel.reward.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.reward.model.dto.ExchangeRecordQueryRequest;
import com.travel.reward.model.entity.ExchangeRecord;
import com.travel.reward.model.vo.ExchangeRecordVO;
import com.travel.reward.service.ExchangeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 15/4/2023 下午 7:50
 */
@RestController
@RequestMapping("/exchange")
@Api(tags = "兑换记录 Controller")
public class ExchangeRecordController {

    @Resource
    private ExchangeRecordService exchangeRecordService;

    /**
     * 分页获取列表（封装类）
     *
     * @param exchangeRecordQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取列表（封装类）")
    @PostMapping("/vo/page/list")
    public BaseResponse<Page<ExchangeRecordVO>> listExchangeVOByPage(@RequestBody ExchangeRecordQueryRequest exchangeRecordQueryRequest) {
        if (exchangeRecordQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = exchangeRecordQueryRequest.getCurrent();
        long size = exchangeRecordQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<ExchangeRecord> exchangeRecordPage = exchangeRecordService.page(new Page<>(current, size),
                exchangeRecordService.getQueryWrapper(exchangeRecordQueryRequest));

        return ResultUtils.success(exchangeRecordService.getExchangeVOPage(exchangeRecordPage));
    }
}
