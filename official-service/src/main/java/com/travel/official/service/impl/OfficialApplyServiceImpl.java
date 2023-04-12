package com.travel.official.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.official.mapper.OfficialMapper;
import com.travel.official.model.dto.official.OfficialQueryRequest;
import com.travel.official.model.dto.officialApply.OfficialApplyQueryRequest;
import com.travel.official.model.entity.Official;
import com.travel.official.model.entity.OfficialApply;
import com.travel.official.model.entity.OfficialDetail;
import com.travel.official.model.vo.OfficialApplyVO;
import com.travel.official.model.vo.OfficialVO;
import com.travel.official.service.OfficialApplyService;
import com.travel.official.mapper.OfficialApplyMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【official_apply(官方申请表)】的数据库操作Service实现
* @createDate 2023-03-22 14:45:55
*/
@Service
public class OfficialApplyServiceImpl extends ServiceImpl<OfficialApplyMapper, OfficialApply>
    implements OfficialApplyService{

    @Resource
    private OfficialMapper officialMapper;

    @DubboReference
    private InnerUserService innerUserService;

    @Override
    public void validOfficial(OfficialApply officialApply) {
        if (officialApply == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String officialName = officialApply.getOfficialName();
        String location = officialApply.getLocation();

        // 创建时，参数不能为空
        ThrowUtils.throwIf(StringUtils.isAnyBlank(officialName, location), ErrorCode.PARAMS_ERROR);

        // 有参数则校验
        if (StringUtils.isNotBlank(officialName) && officialName.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "官方名称过长");
        }
    }

    @Override
    public Long addOfficialApply(OfficialApply officialApply) {
        // 设置 team 的创建人
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        officialApply.setUserId(loginUserId);

        // 初始化官方
        Official official = new Official();
        BeanUtils.copyProperties(officialApply, official);
        officialMapper.insert(official);

        // 添加到数据库中
        boolean saveResult = this.save(officialApply);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);

        return officialApply.getId();
    }

    @Override
    public OfficialApplyVO getOfficialApplyVO(OfficialApply officialApply) {
        OfficialApplyVO officialApplyVO = OfficialApplyVO.objToVo(officialApply);

        // 关联查询用户信息
        Long userId = officialApplyVO.getUserId();
        UserDTO userDTO = null;
        if (userId != null && userId > 0) {
            userDTO = innerUserService.getUser(userId);
        }
        officialApplyVO.setUser(userDTO);

        return officialApplyVO;
    }

    @Override
    public Page<OfficialApplyVO> getOfficialApplyVOPage(Page<OfficialApply> officialApplyPage) {
        List<OfficialApply> officialApplyList = officialApplyPage.getRecords();
        Page<OfficialApplyVO> officialApplyVOPage = new Page<>(officialApplyPage.getCurrent(), officialApplyPage.getSize(), officialApplyPage.getTotal());
        if (CollectionUtils.isEmpty(officialApplyList)) {
            return officialApplyVOPage;
        }

        // 获取用户 id 列表
        Set<Long> userIdSet = officialApplyList.stream().map(officialApply -> officialApply.getUserId()).collect(Collectors.toSet());

        // 获取用户列表
        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);

        // 将用户 id 和用户对应起来
        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));

        // 填充信息
        List<OfficialApplyVO> officialVOList = officialApplyList.stream().map(officialApply -> {
            // 获取官方申请视图体
            OfficialApplyVO officialApplyVO = OfficialApplyVO.objToVo(officialApply);
            // 注入用户到官方视图体内
            Long userId = officialApplyVO.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            officialApplyVO.setUser(userDTO);
            return officialApplyVO;
        }).collect(Collectors.toList());
        officialApplyVOPage.setRecords(officialVOList);
        return officialApplyVOPage;
    }

    @Override
    public QueryWrapper<OfficialApply> getQueryWrapper(OfficialApplyQueryRequest officialApplyQueryRequest) {
        QueryWrapper<OfficialApply> queryWrapper = new QueryWrapper<>();
        if (officialApplyQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询条件中的字段
        String searchText = officialApplyQueryRequest.getSearchText();
        String sortField = officialApplyQueryRequest.getSortField();
        String sortOrder = officialApplyQueryRequest.getSortOrder();
        Long id = officialApplyQueryRequest.getId();
        String officialName = officialApplyQueryRequest.getOfficialName();
        Integer applyState = officialApplyQueryRequest.getApplyState();
        Long userId = officialApplyQueryRequest.getUserId();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("official_name", searchText).or().like("intro", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(officialName), "official_name", officialName);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(applyState), "apply_state", applyState);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}




