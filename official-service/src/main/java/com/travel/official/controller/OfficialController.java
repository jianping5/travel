package com.travel.official.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ErrorCode;
import com.travel.common.common.ResultUtils;
import com.travel.common.constant.TypeConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.utils.UserHolder;
import com.travel.official.model.dto.official.OfficialAddRequest;
import com.travel.official.model.dto.official.OfficialLikeRequest;
import com.travel.official.model.dto.official.OfficialQueryRequest;
import com.travel.official.model.dto.official.OfficialUpdateRequest;
import com.travel.official.model.entity.Official;
import com.travel.official.model.vo.OfficialVO;
import com.travel.official.service.OfficialDetailService;
import com.travel.official.service.OfficialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jianping5
 * @createDate 3/4/2023 下午 3:45
 */
@RestController
@Api(tags = "官方 Controller")
public class OfficialController {

    @Resource
    private OfficialDetailService officialDetailService;

    @Resource
    private OfficialService officialService;

    /**
     * 添加官方介绍
     *
     * @param officialAddRequest
     * @return
     */
    @ApiOperation(value = "添加官方介绍")
    @PostMapping("/add")
    public BaseResponse<Long> addOfficial(@RequestBody OfficialAddRequest officialAddRequest) {
        // 校验请求体
        if (officialAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 赋值官方介绍
        Official official = new Official();
        BeanUtils.copyProperties(officialAddRequest, official);

        // 校验官方
        officialService.validOfficial(official, true);

        return ResultUtils.success(officialService.addOfficial(official));
    }

    /**
     * 更新官方介绍
     *
     * @param officialUpdateRequest
     * @return
     */
    @ApiOperation(value = "更新官方介绍")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateOfficial(@RequestBody OfficialUpdateRequest officialUpdateRequest) {
        // 校验官方更新请求体
        if (officialUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 赋值给 official
        Official official = new Official();
        BeanUtils.copyProperties(officialUpdateRequest, official);

        // 校验官方
        officialService.validOfficial(official, true);

        // 判断是否存在
        Official oldOfficial = officialService.getById(official.getId());
        ThrowUtils.throwIf(oldOfficial == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新团队
        User loginUser = UserHolder.getUser();
        official.setUserId(loginUser.getId());
        officialService.updateOfficial(official);

        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取官方视图体（不包含官方详情）
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据 id 获取官方视图体（不包含官方详情）")
    @GetMapping("/vo")
    public BaseResponse<OfficialVO> getOfficialVOById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Official official = officialService.getById(id);
        if (official == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(officialService.getOfficialVO(official));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param officialQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取列表（封装类）")
    @PostMapping("/vo/page/list")
    public BaseResponse<Page<OfficialVO>> listOfficialVOByPage(@RequestBody OfficialQueryRequest officialQueryRequest) {
        if (officialQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = officialQueryRequest.getCurrent();
        long size = officialQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Official> officialPage = officialService.page(new Page<>(current, size),
                officialService.getQueryWrapper(officialQueryRequest));

        return ResultUtils.success(officialService.getOfficialVOPage(officialPage));
    }

    /**
     * 根据 Id 获取官方详情
     */
    @ApiOperation(value = "根据 Id 获取官方详情")
    @GetMapping("/detail")
    public BaseResponse<OfficialVO> getOfficialDetail(long offId, long detailId) {
        // 校验 id
        if (offId <= 0 || detailId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取官方，并将官方详情 id 注入
        Official official = officialService.getById(offId);
        official.setOfficialDetailId(detailId);

        if (official == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(officialService.getOfficialDetail(official));
    }


    /**
     * 获取推荐官方（大众化推荐）
     *
     * @param officialQueryRequest
     * @return
     */
    @ApiOperation(value = "获取推荐官方（大众化推荐）")
    @PostMapping("/rcmd")
    public BaseResponse<List<OfficialVO>> listRcmdOfficialVO(@RequestBody OfficialQueryRequest officialQueryRequest) {
        if (officialQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = officialQueryRequest.getCurrent();
        long size = officialQueryRequest.getPageSize();
        Integer typeId = officialQueryRequest.getTypeId();

        ThrowUtils.throwIf(typeId == null || typeId < 0, ErrorCode.PARAMS_ERROR);

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        List<OfficialVO> officialVOList = officialService.listRcmdOfficialVO(current, size, typeId);

        return ResultUtils.success(officialVOList);
    }


    /**
     * 点赞
     */
    @ApiOperation(value = "点赞官方、官方点评、官方资源")
    @PostMapping("/like")
    public BaseResponse<Boolean> likeOfficial(@RequestBody OfficialLikeRequest officialLikeRequest) {
        // 校验请求体
        if (officialLikeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = officialLikeRequest.getId();
        Integer type = officialLikeRequest.getType();
        Integer status = officialLikeRequest.getStatus();

        // 校验请求参数
        if (id == null || id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (type == null || !TypeConstant.containsKey(type)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (status == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 点赞
        officialService.likeOfficial(id, type, status);

        return ResultUtils.success(true);
    }

}
