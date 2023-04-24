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
import com.travel.official.model.dto.officialResource.OfficialResourceAddRequest;
import com.travel.official.model.dto.officialResource.OfficialResourceQueryRequest;
import com.travel.official.model.dto.officialResource.OfficialResourceUpdateRequest;
import com.travel.official.model.entity.OfficialResource;
import com.travel.official.model.vo.OfficialResourceVO;
import com.travel.official.service.OfficialResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 官方资源 Controller
 * @author jianping5
 * @createDate 3/4/2023 下午 8:10
 */
@RestController
@RequestMapping("/resource")
@Api(tags = "官方资源 Controller")
public class OfficialResourceController {

    @Resource
    private OfficialResourceService officialResourceService;

    /**
     * 添加官方资源
     *
     * @param officialResourceAddRequest
     * @return
     */
    @ApiOperation(value = "添加官方资源")
    @PostMapping("/add")
    public BaseResponse<Long> addOfficialResource(@RequestBody OfficialResourceAddRequest officialResourceAddRequest) {
        // 校验请求体
        if (officialResourceAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 请求体的内容赋值到 OfficialResource 中
        OfficialResource officialResource = new OfficialResource();
        BeanUtils.copyProperties(officialResourceAddRequest, officialResource);

        // 校验 OfficialResource 信息是否合法
        officialResourceService.validOfficialResource(officialResource, true);

        // 添加官方资源，并返回 id
        return ResultUtils.success(officialResourceService.addOfficialResource(officialResource));
    }

    /**
     * 下架官方资源
     *
     * @param deleteRequest
     * @return
     */
    @ApiOperation(value = "下架官方资源")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteOfficialResource(@RequestBody DeleteRequest deleteRequest) {
        // 校验删除请求体
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取删除的 OfficialResource id
        long id = deleteRequest.getId();

        // 判断是否存在
        OfficialResource oldOfficialResource = officialResourceService.getById(id);
        ThrowUtils.throwIf(oldOfficialResource == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();

        // 仅本人或管理员可删除
        if (!oldOfficialResource.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(2)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = officialResourceService.deleteOfficialResource(oldOfficialResource);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    /**
     * 更新官方资源
     *
     * @param officialResourceUpdateRequest
     * @return
     */
    @ApiOperation(value = "更新官方资源")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateOfficialResource(@RequestBody OfficialResourceUpdateRequest officialResourceUpdateRequest) {
        // 校验官方资源更新请求体
        if (officialResourceUpdateRequest == null || officialResourceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 将团队更新请求体的内容赋值给 官方资源
        OfficialResource officialResource = new OfficialResource();
        BeanUtils.copyProperties(officialResourceUpdateRequest, officialResource);

        // 参数校验
        officialResourceService.validOfficialResource(officialResource, false);
        long id = officialResourceUpdateRequest.getId();

        // 判断是否存在
        OfficialResource oldOfficialResource = officialResourceService.getById(id);
        ThrowUtils.throwIf(oldOfficialResource == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新官方资源
        User loginUser = UserHolder.getUser();
        officialResource.setUserId(loginUser.getId());
        officialResourceService.updateOfficialResource(officialResource);

        return ResultUtils.success(true);
    }


    /**
     * 根据 id 获取官方资源资源
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据 id 获取官方资源")
    @GetMapping("/get/vo")
    public BaseResponse<OfficialResourceVO> getOfficialResourceVOById(long id) {
        // 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        OfficialResource officialResource = officialResourceService.getById(id);
        if (officialResource == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(officialResourceService.getOfficialResourceVO(officialResource));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param officialResourceQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取列表（封装类）")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<OfficialResourceVO>> listOfficialResourceVOByPage(@RequestBody OfficialResourceQueryRequest officialResourceQueryRequest) {
        if (officialResourceQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = officialResourceQueryRequest.getCurrent();
        long size = officialResourceQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<OfficialResource> officialResourcePage = officialResourceService.page(new Page<>(current, size),
                officialResourceService.getQueryWrapper(officialResourceQueryRequest));

        return ResultUtils.success(officialResourceService.getOfficialResourceVOPage(officialResourcePage));
    }

    /**
     * 根据 Id 获取官方资源详情
     */
    @ApiOperation(value = "根据 Id 获取官方资源详情")
    @GetMapping("/detail")
    public BaseResponse<OfficialResourceVO> getOfficialResourceDetail(long offResId, long detailId) {
        // 校验 id
        if (offResId <= 0 || detailId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取官方资源，并将官方资源详情的 id 注入
        OfficialResource officialResource = officialResourceService.getById(offResId);
        officialResource.setResourceDetailId(detailId);

        if (officialResource == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(officialResourceService.getOfficialDetail(officialResource));
    }
}
