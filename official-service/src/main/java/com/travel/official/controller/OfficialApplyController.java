package com.travel.official.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.official.model.dto.officialApply.OfficialApplyAddRequest;
import com.travel.official.model.dto.officialApply.OfficialApplyQueryRequest;
import com.travel.official.model.entity.OfficialApply;
import com.travel.official.model.vo.OfficialApplyVO;
import com.travel.official.service.OfficialApplyService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jianping5
 * @createDate 3/4/2023 下午 4:33
 */
@RestController
@RequestMapping("/official-apply")
public class OfficialApplyController {

    @Resource
    private OfficialApplyService officialApplyService;

    /**
     * 申请成为官方
     *
     * @param officialApplyAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addOfficial(@RequestBody OfficialApplyAddRequest officialApplyAddRequest) {
        // 校验请求体
        if (officialApplyAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 officialApply 中
        OfficialApply officialApply = new OfficialApply();
        BeanUtils.copyProperties(officialApplyAddRequest, officialApply);

        // 校验 officialApply 信息是否合法
        officialApplyService.validOfficial(officialApply);

        // 添加官方申请，并返回 id
        return ResultUtils.success(officialApplyService.addOfficialApply(officialApply));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param officialApplyQueryRequest
     * @return
     */
    @PostMapping("/vo/page/list")
    public BaseResponse<Page<OfficialApplyVO>> listOfficialVOByPage(@RequestBody OfficialApplyQueryRequest officialApplyQueryRequest) {
        if (officialApplyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = officialApplyQueryRequest.getCurrent();
        long size = officialApplyQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<OfficialApply> officialApplyPage = officialApplyService.page(new Page<>(current, size),
                officialApplyService.getQueryWrapper(officialApplyQueryRequest));

        return ResultUtils.success(officialApplyService.getOfficialApplyVOPage(officialApplyPage));
    }
}
