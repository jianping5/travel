package com.travel.reward.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.reward.model.dto.ConsumeRecordQueryRequest;
import com.travel.reward.model.entity.ConsumeRecord;
import com.travel.reward.model.vo.ConsumeRecordVO;
import com.travel.reward.service.ConsumeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 15/4/2023 下午 7:49
 */
@RestController
@RequestMapping("/consume")
@Api(tags = "消费记录 Controller")
public class ConsumeRecordController {

    @Resource
    private ConsumeRecordService consumeRecordService;

    /**
     * 分页获取列表（封装类）
     *
     * @param consumeRecordQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取列表（封装类）")
    @PostMapping("/vo/page/list")
    public BaseResponse<Page<ConsumeRecordVO>> listConsumeVOByPage(@RequestBody ConsumeRecordQueryRequest consumeRecordQueryRequest) {
        if (consumeRecordQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = consumeRecordQueryRequest.getCurrent();
        long size = consumeRecordQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<ConsumeRecord> consumeRecordPage = consumeRecordService.page(new Page<>(current, size),
                consumeRecordService.getQueryWrapper(consumeRecordQueryRequest));

        return ResultUtils.success(consumeRecordService.getConsumeVOPage(consumeRecordPage));
    }
}
