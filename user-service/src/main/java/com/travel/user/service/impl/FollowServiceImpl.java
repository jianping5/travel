package com.travel.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.user.mapper.FollowMapper;
import com.travel.user.model.dto.FollowVO;
import com.travel.user.model.entity.Follow;
import com.travel.user.model.request.FollowQueryRequest;
import com.travel.user.model.request.FollowRequest;
import com.travel.user.service.FollowService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【follow(关注表)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
    implements FollowService{
    @DubboReference
    private InnerUserService innerUserService;


    @Override
    public void validFollow(FollowRequest followRequest, boolean add) {
        if (followRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = followRequest.getUserId();
        Long followUserId = followRequest.getFollowUserId();
        Integer followState = followRequest.getFollowState();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(ObjectUtils.anyNull(userId,followUserId,followState),ErrorCode.PARAMS_ERROR);
        }
    }
    
    @Override
    public Page<FollowVO> getFollowVOPage(Page<Follow> followPage) {
        if(followPage == null){
            return null;
        }
        List<Follow> followList = followPage.getRecords();
        Page<FollowVO> followVOPage = new Page<>(followPage.getCurrent(), followPage.getSize(), followPage.getTotal());
        if (CollectionUtils.isEmpty(followList)) {
            return followVOPage;
        }
        //填充信息
        List<FollowVO> followVOList = getFollowVOList(followList);
        followVOPage.setRecords(followVOList);
        return followVOPage;
    }

    /**
     * 根据文章列表获取文章视图体列表
     * @param followList
     * @return
     */
    public List<FollowVO> getFollowVOList(List<Follow> followList) {
        // 获取用户 id 列表
        Set<Long> userIdSet = followList.stream().map(Follow::getFollowUserId).collect(Collectors.toSet());

        // 获取用户列表
        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);

        // 将用户 id 和用户对应起来
        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));
        // 填充信息
        List<FollowVO> followVOList = followList.stream().map(follow -> {
            // 获取官方视图体
            FollowVO followVO = FollowVO.objToVo(follow);
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(follow.getFollowUserId())) {
                userDTO = userIdUserListMap.get(follow.getFollowUserId()).get(0);
            }
            followVO.setFollowUserAvatar(userDTO.getUserAvatar());
            followVO.setFollowUserName(userDTO.getUserName());
            return followVO;
        }).collect(Collectors.toList());
        return followVOList;
    }

    @Override
    public Page<Follow> queryFollow(FollowQueryRequest followQueryRequest) {
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        if (followQueryRequest == null) {
            return null;
        }
        // 获取查询条件中的字段
        Long userId = followQueryRequest.getUserId();
        String sortField = followQueryRequest.getSortField();
        String sortOrder = followQueryRequest.getSortOrder();
        long pageSize = followQueryRequest.getPageSize();
        long current = followQueryRequest.getCurrent();

        //拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq("follow_state", 1);

        //排序
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return page(new Page<>(current, pageSize), queryWrapper);
    }

    @Override
    public Follow executeFollow(Follow follow) {
        QueryWrapper<Follow> eq = new QueryWrapper<Follow>()
                .eq("user_id", follow.getUserId())
                .eq("follow_user_id", follow.getFollowUserId());
        Follow oldFollow = getOne(eq);
        if(oldFollow==null){
            boolean save = save(follow);
            Follow newFollow = this.getById(follow.getId());
            // todo:更新关注数
            return newFollow;
        }else {
            oldFollow.setFollowState(follow.getFollowState());
            // todo:更新关注数
            boolean update = updateById(oldFollow);
            return oldFollow;
        }
    }
}




