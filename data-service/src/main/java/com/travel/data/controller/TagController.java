package com.travel.data.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travel.common.common.BaseResponse;
import com.travel.common.common.ResultUtils;
import com.travel.data.model.entity.Tag;
import com.travel.data.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jianping5
 * @createDate 13/4/2023 下午 6:55
 */
@RestController
@RequestMapping("/tag")
@Api(tags = "标签 Controller")
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 获取默认标签
     * @return
     */
    @ApiOperation(value = "获取默认标签")
    @GetMapping("/list")
    public BaseResponse<List<Tag>> listDefaultTag() {
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("is_customized", 0);
        List<Tag> tagList = tagService.list(tagQueryWrapper);
        return ResultUtils.success(tagList);
    }

    // @PostMapping("/add")
    // public BaseResponse<Boolean> addCustomizedTag(@RequestBody TagAddRequest tagAddRequest) {
    //     if (tagAddRequest == null) {
    //         throw new BusinessException(ErrorCode.PARAMS_ERROR);
    //     }
    //     Tag tag = new Tag();
    //     BeanUtils.copyProperties(tagAddRequest, tag);
    //     boolean save = tagService.save(tag);
    //     ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
    //     return ResultUtils.success(true);
    // }


}
