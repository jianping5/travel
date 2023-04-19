package com.travel.reward.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.reward.model.entity.RewardTask;
import com.travel.reward.service.RewardTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jianping5
 * @createDate 15/4/2023 下午 7:50
 */
@RestController
@RequestMapping("/reward-task")
public class RewardTaskController {

    @Resource
    private RewardTaskService rewardTaskService;

    /**
     * 分页获取列表（封装类）
     *
     * @param taskType
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<RewardTask>> listTaskVO(@RequestParam Integer taskType) {
        if (taskType == null || taskType < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<RewardTask> rewardTaskQueryWrapper = new QueryWrapper<>();
        rewardTaskQueryWrapper.eq("task_type", taskType);

        return ResultUtils.success(rewardTaskService.list(rewardTaskQueryWrapper));
    }
}
