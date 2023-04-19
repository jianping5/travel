package com.travel.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.user.model.entity.History;
import com.travel.user.model.request.HistoryQueryRequest;
import com.travel.user.service.HistoryService;
import com.travel.user.mapper.HistoryMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【history(历史记录表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History>
    implements HistoryService{

    @DubboReference
    private InnerUserService innerUserService;

    @Override
    public void validHistory(History history, boolean add) {
        if (history == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = history.getUserId();
        Integer historyObjType = history.getHistoryObjType();
        Long historyObjId = history.getHistoryObjId();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(ObjectUtils.anyNull(userId,historyObjType,historyObjId),ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(historyObjType<0||historyObjId<=0,ErrorCode.PARAMS_ERROR);
        }
    }


    @Override
    public Page<History> queryHistory(HistoryQueryRequest historyQueryRequest) {
        QueryWrapper<History> queryWrapper = new QueryWrapper<>();
        if (historyQueryRequest == null) {
            return null;
        }
        // 获取查询条件中的字段
        Long userId = historyQueryRequest.getUserId();
        String sortField = historyQueryRequest.getSortField();
        String sortOrder = historyQueryRequest.getSortOrder();
        long pageSize = historyQueryRequest.getPageSize();
        long current = historyQueryRequest.getCurrent();

        //拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq("is_deleted", 0);

        //排序
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);


        return page(new Page<>(current, pageSize), queryWrapper);
    }

    @Override
    public History addHistory(History history) {
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        // 设置用户 id
        history.setUserId(loginUserId);
        QueryWrapper<History> eq = new QueryWrapper<History>()
                .eq("user_id", loginUserId)
                .eq("history_obj_type",history.getHistoryObjType())
                .eq("history_obj_id",history.getHistoryObjId());
        History oldHistory = getOne(eq);
        if(oldHistory==null){
            boolean save = save(history);
            History newHistory = this.getById(history.getId());
            return newHistory;
        }else {
            oldHistory.setIsDeleted(0);
            boolean update = updateById(oldHistory);
            return oldHistory;
        }
    }

    @Override
    public boolean deleteHistory(History history) {
        // 将历史记录设置为已删除
        history.setIsDeleted(1);
        boolean result = this.updateById(history);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }
}




