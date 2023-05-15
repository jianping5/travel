package com.travel.official.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.constant.TypeConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.official.mapper.OfficialResourceMapper;
import com.travel.official.model.dto.officialResource.OfficialResourceQueryRequest;
import com.travel.official.model.entity.OfficialResource;
import com.travel.official.model.entity.ResourceDetail;
import com.travel.official.model.vo.OfficialResourceVO;
import com.travel.official.service.OfficialResourceService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【official_resource(官方资源表)】的数据库操作Service实现
* @createDate 2023-03-22 14:45:55
*/
@Service
public class OfficialResourceServiceImpl extends ServiceImpl<OfficialResourceMapper, OfficialResource>
    implements OfficialResourceService{

    @Resource
    private ResourceDetailServiceImpl resourceDetailService;

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void validOfficialResource(OfficialResource officialResource, boolean add) {
        if (officialResource == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = officialResource.getTitle();
        String detail = officialResource.getDetail();
        // todo: 这两者可以考虑使用默认值（若用户不传）
        String coverUrl = officialResource.getCoverUrl();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, detail), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "官方资源名称过长");
        }
    }

    @Override
    public Long addOfficialResource(OfficialResource officialResource) {
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long officialId = loginUser.getOfficialId();
        Integer typeId = loginUser.getTypeId();
        // 设置用户 id
        officialResource.setUserId(loginUserId);
        // 设置官方 id
        officialResource.setOfficialId(officialId);
        // 设置官方类型
        officialResource.setTypeId(typeId);

        // todo：暂时随机注入浏览量
        officialResource.setViewCount(RandomUtils.nextInt(0, 1000));

        // 添加到数据库中
        boolean saveResult = this.save(officialResource);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);

        // 添加官方资源详情
        ResourceDetail resourceDetail = new ResourceDetail();
        resourceDetail.setDetail(officialResource.getDetail());
        resourceDetail.setOfficialResourceId(officialResource.getId());
        resourceDetailService.addResourceDetail(resourceDetail);

        // 返回该官方资源 id
        return officialResource.getId();
    }

    @Override
    public boolean deleteOfficialResource(OfficialResource officialResource) {
        // todo: 考虑加事务，是否需要硬删除对应的官方资源详情

        // 将官方资源状态设置为已下架
        officialResource.setResourceState(2);
        boolean result = this.updateById(officialResource);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return true;
    }

    @Override
    public boolean updateOfficialResource(OfficialResource officialResource) {
        // todo：需要判重吗？

        // 判断当前用户是否为当前官方资源的创建人
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long officialResourceUserId = officialResource.getUserId();
        ThrowUtils.throwIf(!loginUserId.equals(officialResourceUserId), ErrorCode.NO_AUTH_ERROR);

        // 更新官方资源
        boolean updateResult = this.updateById(officialResource);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);

        // 更新官方资源详情
        ResourceDetail resourceDetail = new ResourceDetail();
        resourceDetail.setId(officialResource.getResourceDetailId());
        resourceDetail.setOfficialResourceId(officialResource.getId());
        resourceDetail.setDetail(officialResource.getDetail());
        resourceDetailService.updateResourceDetail(resourceDetail);

        return true;
    }

    @Override
    public OfficialResourceVO getOfficialResourceVO(OfficialResource officialResource) {
        OfficialResourceVO officialResourceVO = OfficialResourceVO.objToVo(officialResource);

        // 关联查询官方用户信息
        Long userId = officialResource.getUserId();
        UserDTO userDTO = null;
        if (userId != null && userId > 0) {
            userDTO = innerUserService.getUser(userId);
        }
        officialResourceVO.setUser(userDTO);

        // 关联官方资源详情 id
        Long officialResourceId = officialResource.getId();
        QueryWrapper<ResourceDetail> resourceDetailQueryWrapper = new QueryWrapper<>();
        resourceDetailQueryWrapper.eq("official_resource_id", officialResourceId);
        ResourceDetail resourceDetail = resourceDetailService.getOne(resourceDetailQueryWrapper);
        officialResourceVO.setResourceDetailId(resourceDetail.getId());

        return officialResourceVO;
    }

    @Override
    public Page<OfficialResourceVO> getOfficialResourceVOPage(Page<OfficialResource> officialResourcePage) {
        List<OfficialResource> officialResourceList = officialResourcePage.getRecords();
        Page<OfficialResourceVO> officialResourceVOPage = new Page<>(officialResourcePage.getCurrent(), officialResourcePage.getSize(), officialResourcePage.getTotal());
        if (CollectionUtils.isEmpty(officialResourceList)) {
            return officialResourceVOPage;
        }

        // 根据官方资源列表获取官方资源视图体列表
        List<OfficialResourceVO> officialResourceVOList = getOfficialResourceVOList(officialResourceList);

        officialResourceVOPage.setRecords(officialResourceVOList);
        return officialResourceVOPage;
    }

    @Override
    public QueryWrapper<OfficialResource> getQueryWrapper(OfficialResourceQueryRequest officialResourceQueryRequest) {
        QueryWrapper<OfficialResource> queryWrapper = new QueryWrapper<>();
        if (officialResourceQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询条件中的字段
        String searchText = officialResourceQueryRequest.getSearchText();
        String sortField = officialResourceQueryRequest.getSortField();
        String sortOrder = officialResourceQueryRequest.getSortOrder();
        Long id = officialResourceQueryRequest.getId();
        String title = officialResourceQueryRequest.getTitle();
        String intro = officialResourceQueryRequest.getIntro();
        Long userId = officialResourceQueryRequest.getUserId();
        Long officialId = officialResourceQueryRequest.getOfficialId();
        Integer resourceState = officialResourceQueryRequest.getResourceState();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("intro", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(intro), "intro", intro);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(officialId), "official_id", officialId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resourceState), "resource_state", 0);
        return queryWrapper;
    }

    @Override
    public OfficialResourceVO getOfficialDetail(OfficialResource officialResource) {
        OfficialResourceVO officialResourceVO = OfficialResourceVO.objToVo(officialResource);

        // 关联查询官方用户信息
        Long userId = officialResource.getUserId();
        UserDTO userDTO = null;
        if (userId != null && userId > 0) {
            userDTO = innerUserService.getUser(userId);
        }
        officialResourceVO.setUser(userDTO);

        // 关联官方资源详情
        // 获取官方资源详情 id
        Long resourceDetailId = officialResource.getResourceDetailId();
        // 获取官方详情
        ResourceDetail resourceDetail = resourceDetailService.getById(resourceDetailId);

        // 注入到官方详情视图体中
        officialResourceVO.setResourceDetailId(resourceDetail.getId());
        officialResourceVO.setDetail(resourceDetail.getDetail());

        // 若已登录，点赞状态
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();

        // 是否点赞
        String officialResourceLike = String.format("travel:official:like:%d:%d", TypeConstant.OFFICIAL_RESOURCE.getTypeIndex(), officialResource.getId());
        RSet<Long> officialResourceLikeSet = redissonClient.getSet(officialResourceLike);
        if (officialResourceLikeSet.contains(loginUserId)) {
            officialResourceVO.setIsLiked(1);
        } else {
            officialResourceVO.setIsLiked(0);
        }

        return officialResourceVO;
    }

    @Override
    public List<OfficialResourceVO> getOfficialResourceVOList(List<OfficialResource> officialResourceList) {

        // 获取用户 id 列表
        Set<Long> userIdSet = officialResourceList.stream().map(officialResource -> officialResource.getUserId()).collect(Collectors.toSet());

        // 获取用户列表
        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);

        // 将用户 id 和用户对应起来
        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));

        // 获取 map（官方资源 id，官方资源详情 List）
        Set<Long> officialResourceIdSet = officialResourceList.stream().map(OfficialResource::getId).collect(Collectors.toSet());
        QueryWrapper<ResourceDetail> resourceDetailQueryWrapper = new QueryWrapper<>();
        resourceDetailQueryWrapper.in(CollectionUtils.isNotEmpty(officialResourceIdSet), "official_resource_id", officialResourceIdSet);
        List<ResourceDetail> resourceDetailList = resourceDetailService.list(resourceDetailQueryWrapper);
        Map<Long, List<ResourceDetail>> officialResourceIdDetailListMap = resourceDetailList.stream().collect(Collectors.groupingBy(ResourceDetail::getOfficialResourceId));

        // 填充信息
        List<OfficialResourceVO> officialResourceVOList = officialResourceList.stream().map(officialResource -> {
            // 获取官方资源视图体
            OfficialResourceVO officialResourceVO = OfficialResourceVO.objToVo(officialResource);

            // 注入用户到官方资源视图体内
            Long userId = officialResource.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            officialResourceVO.setUser(userDTO);


            // 注入官方资源详情 id 到官方视图体内
            Long officialResourceId = officialResource.getId();
            ResourceDetail resourceDetail = null;
            if (officialResourceIdDetailListMap.containsKey(officialResourceId)) {
                resourceDetail = officialResourceIdDetailListMap.get(officialResourceId).get(0);
            }
            officialResourceVO.setResourceDetailId(resourceDetail.getId());

            return officialResourceVO;
        }).collect(Collectors.toList());

        return officialResourceVOList;
    }

}




