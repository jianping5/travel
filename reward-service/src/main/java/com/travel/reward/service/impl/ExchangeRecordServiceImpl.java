package com.travel.reward.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.constant.CommonConstant;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerOfficialService;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.reward.mapper.ExchangeRecordMapper;
import com.travel.reward.model.dto.ExchangeRecordQueryRequest;
import com.travel.reward.model.entity.ExchangeRecord;
import com.travel.reward.model.vo.ExchangeRecordVO;
import com.travel.reward.service.ExchangeRecordService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【exchange_record(兑换记录表)】的数据库操作Service实现
* @createDate 2023-03-22 14:41:35
*/
@Service
public class ExchangeRecordServiceImpl extends ServiceImpl<ExchangeRecordMapper, ExchangeRecord>
    implements ExchangeRecordService{

    @DubboReference
    private InnerOfficialService innerOfficialService;

    @DubboReference
    private InnerUserService innerUserService;

    @Override
    public QueryWrapper<ExchangeRecord> getQueryWrapper(ExchangeRecordQueryRequest exchangeRecordQueryRequest) {
        QueryWrapper<ExchangeRecord> queryWrapper = new QueryWrapper<>();
        if (exchangeRecordQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询条件中的字段
        String searchText = exchangeRecordQueryRequest.getSearchText();
        String sortField = exchangeRecordQueryRequest.getSortField();
        String sortOrder = exchangeRecordQueryRequest.getSortOrder();
        Long id = exchangeRecordQueryRequest.getId();

        // 获取当前登录用户
        User loginUser = UserHolder.getUser();
        Long userId = loginUser.getId();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("certificate", searchText);
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);


        // 若为官方，则查询当前官方下的兑换记录
        Integer isOfficial = exchangeRecordQueryRequest.getIsOfficial();
        if (isOfficial == 1) {
            // 根据当前用户 id 从周边表获取 [周边 id 列表]
            List<Long> derivativeIdList = innerOfficialService.listDerivativeId(userId);

            // 构造器 in [周边 id 列表]
            queryWrapper.in(CollectionUtils.isNotEmpty(derivativeIdList), "derivative_id", derivativeIdList);
        } else {
            queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        }

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);


        return queryWrapper;
    }

    @Override
    public Page<ExchangeRecordVO> getExchangeVOPage(Page<ExchangeRecord> exchangeRecordPage) {
        List<ExchangeRecord> exchangeRecordList = exchangeRecordPage.getRecords();
        Page<ExchangeRecordVO> exchangeRecordVOPage = new Page<>(exchangeRecordPage.getCurrent(), exchangeRecordPage.getSize(), exchangeRecordPage.getTotal());
        if (CollectionUtils.isEmpty(exchangeRecordList)) {
            return exchangeRecordVOPage;
        }

        // 根据兑换记录列表获取兑换记录视图体列表
        List<ExchangeRecordVO> exchangeRecordVOList = getExchangeVOList(exchangeRecordList);

        exchangeRecordVOPage.setRecords(exchangeRecordVOList);
        return exchangeRecordVOPage;
    }

    /**
     * 根据兑换记录列表获取兑换记录视图体列表
     * @param exchangeRecordList
     * @return
     */
    public List<ExchangeRecordVO> getExchangeVOList(List<ExchangeRecord> exchangeRecordList) {
        // 获取用户 id 列表
        Set<Long> userIdSet = exchangeRecordList.stream().map(ExchangeRecord::getUserId).collect(Collectors.toSet());

        // 获取用户列表
        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);

        // 将用户 id 和用户对应起来
        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));

        // 填充信息
        List<ExchangeRecordVO> exchangeRecordVOList = exchangeRecordList.stream().map(exchangeRecord -> {
            // 获取官方视图体
            ExchangeRecordVO exchangeRecordVO = ExchangeRecordVO.objToVo(exchangeRecord);
            // 注入用户到兑换记录视图体内
            Long userId = exchangeRecord.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            exchangeRecordVO.setUser(userDTO);

            return exchangeRecordVO;
        }).collect(Collectors.toList());

        return exchangeRecordVOList;
    }
}




