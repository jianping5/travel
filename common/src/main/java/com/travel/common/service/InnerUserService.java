package com.travel.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.dto.user.UserQueryRequest;
import com.travel.common.model.vo.UserVDTO;

import java.util.List;
import java.util.Set;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 8:57
 */
public interface InnerUserService{

    /**
     * 根据用户 id 获取 用户
     * @param id
     * @return
     */
    UserDTO getUser(Long id);

    /**
     * 根据 用户 id 数组获取用户数组
     * @param userIdList
     * @return
     */
    List<UserDTO> listByIds(Set<Long> userIdList);

    /**
     * todo: 添加团队 id 到用户表中的团队字段
     * @param userId
     * @param teamId
     * @return
     */
    boolean addUserTeamId(Long userId, Long teamId);

    /**
     * 判断当前用户是否已在该团队
     * @param loginUserId
     * @param teamId
     * @return
     */
    boolean isJoined(Long loginUserId, Long teamId);

    /**
     * 更改团队（0：加入团队 1：退出团队 2：踢出团队）
     * @param userId
     * @param teamId
     * @param joinOrQuitOrKick
     * @return
     */
    boolean changeTeam(Long userId, Long teamId, Integer joinOrQuitOrKick);

    /**
     * 根据用户 id 获取团队 id 列表（json 字符串）
     * @param userId
     * @return
     */
    String getTeamIdStr(Long userId);

    /**
     * 更新点赞记录表
     * @param loginUserId
     * @param type
     * @param id
     * @param status
     * @return
     */
    boolean updateOfficialLike(long loginUserId, int type, long id, int status);

    /**
     * 获取团队成员
     * @param teamId
     * @return
     */
    List<UserDTO> listUserByTeamId(Long teamId);
    /**
     * 添加历史记录
     */
    boolean addHistory(Long userId,Integer historyObjType,Long historyObjId);

    /**
     * 从 ES 中搜索分页的数据
     * @param userQueryRequest
     * @return
     */
    Page<UserVDTO> searchFromEs(UserQueryRequest userQueryRequest);

    /**
     * 消耗代币
     * @param userId
     * @param token
     * @param isAdd
     * @return
     */
    boolean updateToken(Long userId, Integer token, boolean isAdd);

    /**
     * 更改用户的 typeId
     * @param userId
     * @param typeId
     * @return
     */
    boolean changeUserTypeId(Long userId, int typeId);

}
