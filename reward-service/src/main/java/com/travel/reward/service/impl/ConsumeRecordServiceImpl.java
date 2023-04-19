package com.travel.reward.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.constant.CommonConstant;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.reward.mapper.ConsumeRecordMapper;
import com.travel.reward.model.dto.ConsumeRecordQueryRequest;
import com.travel.reward.model.entity.ConsumeRecord;
import com.travel.reward.model.vo.ConsumeRecordVO;
import com.travel.reward.service.ConsumeRecordService;
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
* @description 针对表【consume_record(消费记录表)】的数据库操作Service实现
* @createDate 2023-03-22 14:41:35
*/
@Service
public class ConsumeRecordServiceImpl extends ServiceImpl<ConsumeRecordMapper, ConsumeRecord>
    implements ConsumeRecordService{

    @DubboReference
    private InnerUserService innerUserService;

    @Override
    public QueryWrapper<ConsumeRecord> getQueryWrapper(ConsumeRecordQueryRequest consumeRecordQueryRequest) {
        QueryWrapper<ConsumeRecord> queryWrapper = new QueryWrapper<>();
        if (consumeRecordQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询条件中的字段
        String searchText = consumeRecordQueryRequest.getSearchText();
        String sortField = consumeRecordQueryRequest.getSortField();
        String sortOrder = consumeRecordQueryRequest.getSortOrder();
        Long id = consumeRecordQueryRequest.getId();
        Long userId = consumeRecordQueryRequest.getUserId();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("content", searchText);
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<ConsumeRecordVO> getConsumeVOPage(Page<ConsumeRecord> consumeRecordPage) {
        List<ConsumeRecord> consumeRecordList = consumeRecordPage.getRecords();
        Page<ConsumeRecordVO> consumeRecordVOPage = new Page<>(consumeRecordPage.getCurrent(), consumeRecordPage.getSize(), consumeRecordPage.getTotal());
        if (CollectionUtils.isEmpty(consumeRecordList)) {
            return consumeRecordVOPage;
        }

        // 根据消费记录列表获取消费记录视图体列表
        List<ConsumeRecordVO> consumeRecordVOList = getConsumeVOList(consumeRecordList);

        consumeRecordVOPage.setRecords(consumeRecordVOList);
        return consumeRecordVOPage;
    }

    /**
     * 根据消费记录列表获取消费记录视图体列表
     * @param consumeRecordList
     * @return
     */
    public List<ConsumeRecordVO> getConsumeVOList(List<ConsumeRecord> consumeRecordList) {
        // 获取用户 id 列表
        Set<Long> userIdSet = consumeRecordList.stream().map(ConsumeRecord::getUserId).collect(Collectors.toSet());

        // 获取用户列表
        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);

        // 将用户 id 和用户对应起来
        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));

        // 填充信息
        List<ConsumeRecordVO> consumeRecordVOList = consumeRecordList.stream().map(consumeRecord -> {
            // 获取官方视图体
            ConsumeRecordVO consumeRecordVO = ConsumeRecordVO.objToVo(consumeRecord);
            // 注入用户到消费记录视图体内
            Long userId = consumeRecord.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            consumeRecordVO.setUser(userDTO);

            return consumeRecordVO;
        }).collect(Collectors.toList());

        return consumeRecordVOList;
    }
}




