package com.travel.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.user.model.entity.History;
import com.travel.user.model.request.HistoryAddRequest;
import com.travel.user.model.request.HistoryQueryRequest;
import com.travel.user.service.HistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zgy
 * @createDate 22/3/2023 下午 3:10
 */
@RestController
@RequestMapping("/history")
public class HistoryController {
    @Resource
    private HistoryService historyService;

    @PostMapping("/history/add")
    public BaseResponse<Long> addHistory(@RequestBody HistoryAddRequest historyAddRequest) {
        // 校验请求体
        if (historyAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 History 中
        History history = new History();
        BeanUtils.copyProperties(historyAddRequest, history);

        // 校验 History 信息是否合法
        historyService.validHistory(history, true);

        // 添加history
        History newHistory = historyService.addHistory(history);

        // 获取history id
        long newHistoryId = newHistory.getId();

        return ResultUtils.success(newHistoryId);
    }

    @PostMapping("/history/delete")
    public BaseResponse<Boolean> deleteHistory(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 History id
        long id = deleteRequest.getId();

        // 判断是否存在
        History oldPost = historyService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = historyService.deleteHistory(oldPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    @PostMapping("/history/list/page")
    public BaseResponse<Page<History>> listHistoryByPage(@RequestBody HistoryQueryRequest historyQueryRequest) {
        if (historyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long size = historyQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(historyService.queryHistory(historyQueryRequest));
    }

}
