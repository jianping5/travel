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
import com.travel.official.mapper.NotificationMapper;
import com.travel.official.model.dto.notification.NotificationQueryRequest;
import com.travel.official.model.entity.Notification;
import com.travel.official.model.vo.NotificationVO;
import com.travel.official.service.NotificationService;
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
* @description 针对表【notification(资讯通知表)】的数据库操作Service实现
* @createDate 2023-04-06 15:41:30
*/
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
    implements NotificationService{

    @DubboReference
    private InnerUserService innerUserService;

    @Override
    public void validNotification(Notification notification, boolean add) {
        if (notification == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = notification.getTitle();
        String detail = notification.getDetail();
        // todo: 这两者可以考虑使用默认值（若用户不传）
        String coverUrl = notification.getCoverUrl();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, detail), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "资讯通知名称过长");
        }
    }

    @Override
    public Long addNotification(Notification notification) {
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long officialId = loginUser.getOfficialId();
        Integer typeId = loginUser.getTypeId();
        // 设置用户 id
        notification.setUserId(loginUserId);
        // 设置官方 id
        notification.setOfficialId(officialId);
        // 设置官方类型
        notification.setTypeId(typeId);

        // 添加到数据库中
        boolean saveResult = this.save(notification);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);;

        // 返回该资讯通知 id
        return notification.getId();
    }

    @Override
    public boolean deleteNotification(Notification notification) {
        // todo: 考虑加事务，是否需要硬删除对应的官方资源详情

        // 将资讯通知状态设置为已删除
        notification.setNotificationState(2);
        boolean result = this.updateById(notification);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return true;
    }

    @Override
    public boolean updateNotification(Notification notification) {
        // todo：需要判重吗？

        // 判断当前用户是否为当前资讯通知的创建人
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long notificationUserId = notification.getUserId();
        ThrowUtils.throwIf(!loginUserId.equals(notificationUserId), ErrorCode.NO_AUTH_ERROR);

        // 更新资讯通知
        boolean updateResult = this.updateById(notification);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);

        return true;
    }

    @Override
    public NotificationVO getNotificationVO(Notification notification) {
        NotificationVO notificationVO = NotificationVO.objToVo(notification);

        // todo：浏览数 + 1，用户行为记录

        // 关联查询官方用户信息
        Long userId = notificationVO.getUserId();
        UserDTO userDTO = null;
        if (userId != null && userId > 0) {
            userDTO = innerUserService.getUser(userId);
        }
        notificationVO.setUser(userDTO);

        return notificationVO;
    }

    @Override
    public Page<NotificationVO> getNotificationVOPage(Page<Notification> notificationPage) {
        List<Notification> notificationList = notificationPage.getRecords();
        Page<NotificationVO> notificationVOPage = new Page<>(notificationPage.getCurrent(), notificationPage.getSize(), notificationPage.getTotal());
        if (CollectionUtils.isEmpty(notificationList)) {
            return notificationVOPage;
        }

        // 根据资讯通知列表获取资讯通知视图体列表
        List<NotificationVO> notificationVOList = getNotificationVOList(notificationList);

        notificationVOPage.setRecords(notificationVOList);
        return notificationVOPage;
    }

    @Override
    public QueryWrapper<Notification> getQueryWrapper(NotificationQueryRequest notificationQueryRequest) {
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        if (notificationQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询条件中的字段
        String searchText = notificationQueryRequest.getSearchText();
        String sortField = notificationQueryRequest.getSortField();
        String sortOrder = notificationQueryRequest.getSortOrder();
        Long id = notificationQueryRequest.getId();
        String title = notificationQueryRequest.getTitle();
        String intro = notificationQueryRequest.getIntro();
        Long userId = notificationQueryRequest.getUserId();
        Integer resourceState = notificationQueryRequest.getNotificationState();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("intro", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(intro), "intro", intro);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resourceState), "notification_state", 0);
        return queryWrapper;
    }

    @Override
    public List<NotificationVO> getNotificationVOList(List<Notification> notificationList) {
        // 获取用户 id 列表
        Set<Long> userIdSet = notificationList.stream().map(Notification::getUserId).collect(Collectors.toSet());

        // 获取用户列表
        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);

        // 将用户 id 和用户对应起来
        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));

        // 填充信息
        List<NotificationVO> notificationVOList = notificationList.stream().map(notification -> {
            // 获取官方资源视图体
            NotificationVO notificationVO = NotificationVO.objToVo(notification);

            // 注入用户到资讯通知视图体内
            Long userId = notification.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            notificationVO.setUser(userDTO);

            return notificationVO;
        }).collect(Collectors.toList());

        return notificationVOList;
    }
}




