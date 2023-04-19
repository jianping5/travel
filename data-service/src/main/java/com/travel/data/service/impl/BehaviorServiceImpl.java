package com.travel.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.exception.BusinessException;
import com.travel.common.model.entity.User;
import com.travel.common.model.vo.SearchVDTO;
import com.travel.common.service.InnerRcmdService;
import com.travel.common.utils.UserHolder;
import com.travel.data.mapper.BehaviorMapper;
import com.travel.data.model.dto.PersonalRcmdRequest;
import com.travel.data.model.entity.Behavior;
import com.travel.data.registry.ServiceRegistry;
import com.travel.data.service.BehaviorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【behavior(用户行为表)】的数据库操作Service实现
* @createDate 2023-03-22 14:47:05
*/
@Service
public class BehaviorServiceImpl extends ServiceImpl<BehaviorMapper, Behavior>
    implements BehaviorService{

    @Resource
    private ServiceRegistry serviceRegistry;

    @Override
    public SearchVDTO listPersonalRcmd(PersonalRcmdRequest personalRcmdRequest) {
        // 获取类型
        Integer rcmdType = personalRcmdRequest.getRcmdType();

        // 获取当前登录用户 id
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();


        if (rcmdType == null || rcmdType < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前页和页的大小
        long current = personalRcmdRequest.getCurrent();
        long pageSize = personalRcmdRequest.getPageSize();

        // 根据类型获取指定的 service
        InnerRcmdService<?> innerRcmdService = serviceRegistry.getServiceByType(rcmdType);

        // todo：推荐算法（得到一组 id）
        QueryWrapper<Behavior> behaviorQueryWrapper = new QueryWrapper<>();
        behaviorQueryWrapper.eq("user_id", loginUserId);
        behaviorQueryWrapper.eq("behavior_obj_type", rcmdType);
        behaviorQueryWrapper.orderByDesc("create_time");
        behaviorQueryWrapper.last("limit 5");
        Set<Long> idList = this.list(behaviorQueryWrapper).stream().map(behavior -> behavior.getBehaviorObjId()).collect(Collectors.toSet());

        // todo：调取指定类型的内部服务的个性化推荐接口
        Page<?> page = innerRcmdService.listPersonalRcmd(idList, current, pageSize);

        SearchVDTO searchVDTO = new SearchVDTO();
        searchVDTO.setDataPage(page);

        return searchVDTO;
    }
}




