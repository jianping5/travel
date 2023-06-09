package com.travel.official.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.DeleteRequest;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.official.model.dto.derivative.DerivativeAddRequest;
import com.travel.official.model.dto.derivative.DerivativeObtainRequest;
import com.travel.official.model.dto.derivative.DerivativeQueryRequest;
import com.travel.official.model.dto.derivative.DerivativeUpdateRequest;
import com.travel.official.model.entity.Derivative;
import com.travel.official.model.entity.Official;
import com.travel.official.model.vo.DerivativeVO;
import com.travel.official.service.DerivativeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jianping5
 * @createDate 30/3/2023 下午 5:16
 */
@RestController
@RequestMapping("/derivative")
@Api(tags = "官方周边 Controller")
public class DerivativeController {

    @Resource
    private DerivativeService derivativeService;

    /**
     * 添加周边
     * @param derivativeAddRequest
     * @return
     */
    @ApiOperation(value = "添加周边")
    @PostMapping("/add")
    public BaseResponse<Long> addDerivative(@RequestBody DerivativeAddRequest derivativeAddRequest) {
        // 校验请求体
        if (derivativeAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 Derivative 中
        Derivative derivative = new Derivative();
        BeanUtils.copyProperties(derivativeAddRequest, derivative);

        // 校验 Derivative 信息是否合法
        derivativeService.validDerivative(derivative, true);

        // 添加周边
        Derivative newDerivative = derivativeService.addDerivative(derivative);

        // 获取周边 id
        long newDerivativeId = newDerivative.getId();

        return ResultUtils.success(newDerivativeId);
    }

    /**
     * 下架周边
     *
     * @param deleteRequest
     * @return
     */
    @ApiOperation(value = "下架周边")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteDerivative(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 Derivative id
        long id = deleteRequest.getId();

        // 判断是否存在
        Derivative oldPost = derivativeService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(2)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = derivativeService.deleteDerivative(oldPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    /**
     * 更新周边
     *
     * @param derivativeUpdateRequest
     * @return
     */
    @ApiOperation(value = "更新周边")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateDerivative(@RequestBody DerivativeUpdateRequest derivativeUpdateRequest) {
        // 校验周边更新请求体
        if (derivativeUpdateRequest == null || derivativeUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将周边更新请求体的内容赋值给 周边
        Derivative derivative = new Derivative();
        BeanUtils.copyProperties(derivativeUpdateRequest, derivative);

        // 参数校验
        derivativeService.validDerivative(derivative, false);
        long id = derivativeUpdateRequest.getId();

        // 判断是否存在
        Derivative oldDerivative = derivativeService.getById(id);
        ThrowUtils.throwIf(oldDerivative == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新周边
        User loginUser = UserHolder.getUser();
        derivative.setUserId(loginUser.getId());
        derivativeService.updateDerivative(derivative);

        return ResultUtils.success(true);
    }


    /**
     * 根据 id 获取周边详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据 id 获取周边详情")
    @GetMapping("/get/vo")
    public BaseResponse<DerivativeVO> getDerivativeVOById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Derivative derivative = derivativeService.getById(id);
        if (derivative == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(derivativeService.getDerivativeVO(derivative));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param derivativeQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取列表（封装类）")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<DerivativeVO>> listDerivativeVOByPage(@RequestBody DerivativeQueryRequest derivativeQueryRequest) {
        if (derivativeQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = derivativeQueryRequest.getCurrent();
        long size = derivativeQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Derivative> derivativePage = derivativeService.page(new Page<>(current, size),
                derivativeService.getQueryWrapper(derivativeQueryRequest));

        return ResultUtils.success(derivativeService.getDerivativeVOPage(derivativePage));
    }




    /**
     * 获取推荐周边（大众化推荐）
     *
     * @param derivativeQueryRequest
     * @return
     */
    @ApiOperation(value = "获取推荐周边（大众化推荐）")
    @PostMapping("/rcmd")
    public BaseResponse<List<DerivativeVO>> listRcmdDerivativeVO(@RequestBody DerivativeQueryRequest derivativeQueryRequest) {
        if (derivativeQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = derivativeQueryRequest.getCurrent();
        long size = derivativeQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        List<DerivativeVO> derivativeVOList = derivativeService.listRcmdDerivativeVO(current, size);

        return ResultUtils.success(derivativeVOList);
    }

    /**
     * 获取周边（购买 or 兑换）
     */
    @ApiOperation(value = "获取周边（购买 or 兑换）")
    @PostMapping("/obtain")
    public BaseResponse<Official> obtainDerivative(@RequestBody DerivativeObtainRequest derivativeObtainRequest) {
        if (derivativeObtainRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = derivativeObtainRequest.getId();

        Official official = derivativeService.obtainDerivative(id);

        return ResultUtils.success(official);
    }
}
