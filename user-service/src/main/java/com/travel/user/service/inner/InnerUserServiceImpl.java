package com.travel.user.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.dto.user.UserQueryRequest;
import com.travel.common.model.vo.UserVDTO;
import com.travel.common.service.InnerUserService;
import com.travel.user.mapper.UserInfoMapper;
import com.travel.user.model.dto.UserEsDTO;
import com.travel.user.model.entity.History;
import com.travel.user.model.entity.User;
import com.travel.user.model.entity.UserInfo;
import com.travel.user.model.entity.UserLike;
import com.travel.user.service.HistoryService;
import com.travel.user.service.UserInfoService;
import com.travel.user.service.UserLikeService;
import com.travel.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 5:46
 */
@Slf4j
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserService userService;

    @Resource
    private UserLikeService userLikeService;

    @Resource
    private HistoryService historyService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private Gson gson;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public UserDTO getUser(Long id) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        queryWrapper.select("user_id", "user_name", "user_avatar");
        UserInfo userInfo = userInfoService.getOne(queryWrapper);

        UserDTO userDTO = new UserDTO(id, userInfo.getUserName(), userInfo.getUserAvatar());
        return userDTO;
    }

    @Override
    public List<UserDTO> listByIds(Set<Long> userIdList) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIdList);
        queryWrapper.select("user_id", "user_name", "user_avatar");
        List<UserInfo> userInfoList = userInfoService.list(queryWrapper);
        List<UserDTO> userDTOList = userInfoList.stream()
                .map(userInfo -> new UserDTO(userInfo.getUserId(), userInfo.getUserName(), userInfo.getUserAvatar()))
                .collect(Collectors.toList());

        return userDTOList;
    }

    @Override
    public boolean addUserTeamId(Long userId, Long teamId) {
        // 根据用户 id 先获取用户，再获取其团队列表字段
        User user = userService.getById(userId);
        String teamIdListStr = user.getTeamId();

        // 将团队列表字段转换为数组
        List<Long> teamIdList = gson.fromJson(teamIdListStr, new TypeToken<List<Long>>() {
        }.getType());

        // 更新数组
        teamIdList.add(teamId);

        // 将数组转换成 Json 字符串并持久化到数据库中
        String teamIdStr = gson.toJson(teamIdList, new TypeToken<List<Long>>() {
        }.getType());

        user.setTeamId(teamIdStr);

        return userService.updateById(user);
    }

    @Override
    public boolean isJoined(Long loginUserId, Long teamId) {
        User user = userService.getById(loginUserId);
        String teamIdStr = user.getTeamId();
        List<Long> teamIdList = gson.fromJson(teamIdStr, new TypeToken<List<Long>>() {}.getType());
        return teamIdList.contains(teamId);
    }

    @Override
    public boolean changeTeam(Long userId, Long teamId, Integer joinOrQuitOrKick) {
        // 获取用户团队 id 列表
        User user = userService.getById(userId);
        String teamIdStr = user.getTeamId();
        List<Long> teamIdList = gson.fromJson(teamIdStr, new TypeToken<List<Long>>() {}.getType());

        // 加入团队
        if (joinOrQuitOrKick == 0) {
            // todo：若原先已在团队中，则报错（如何改进）
            ThrowUtils.throwIf(teamIdList.contains(teamId), ErrorCode.OPERATION_ERROR);

            // 添加当前团队 id
            teamIdList.add(teamId);
        }

        // 退出团队
        if (joinOrQuitOrKick == 1) {
            // 若原先并未在团队中，则报错
            ThrowUtils.throwIf(!teamIdList.contains(teamId), ErrorCode.OPERATION_ERROR);

            // 删除当前团队 id
            teamIdList.remove(teamId);
        }

        // 踢出团队
        if (joinOrQuitOrKick == 2) {
            // 若原先并未在团队中，则报错
            ThrowUtils.throwIf(!teamIdList.contains(teamId), ErrorCode.OPERATION_ERROR);

            // 删除当前团队 id
            teamIdList.remove(teamId);
        }

        // 更新用户团队字段
        String teamIdJson = gson.toJson(teamIdList);

        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", userId);
        userUpdateWrapper.set("team_id", teamIdJson);
        boolean updateResult = userService.update(userUpdateWrapper);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);

        return true;
    }

    @Override
    public String getTeamIdStr(Long userId) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id", userId);
        userQueryWrapper.select("team_id");
        User user = userService.getOne(userQueryWrapper);

        String teamId = user.getTeamId();

        return teamId;
    }

    @Override
    public boolean updateOfficialLike(long loginUserId, int type, long id, int status) {
        // 判断是否存在对应记录
        QueryWrapper<UserLike> userLikeQueryWrapper = new QueryWrapper<>();
        userLikeQueryWrapper.eq("user_id", loginUserId);
        userLikeQueryWrapper.eq("like_obj_type", type);
        userLikeQueryWrapper.eq("like_obj_id", id);
        UserLike oldUserLike = userLikeService.getOne(userLikeQueryWrapper);

        // 若原来不存在点赞记录（点赞）
        if (oldUserLike == null) {
            UserLike userLike = new UserLike();
            userLike.setUserId(loginUserId);
            userLike.setLikeObjType(type);
            userLike.setLikeObjId(id);
            userLike.setLikeState(status);

            boolean save = userLikeService.save(userLike);
            ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        }

        // 更新点赞状态
        UpdateWrapper<UserLike> userLikeUpdateWrapper = new UpdateWrapper<>();
        userLikeUpdateWrapper.eq("user_id", loginUserId);
        userLikeUpdateWrapper.eq("like_obj_type", type);
        userLikeUpdateWrapper.eq("like_obj_id", id);
        userLikeUpdateWrapper.set("like_state", status);
        boolean update = userLikeService.update(userLikeUpdateWrapper);
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);

        return true;
    }

    @Override
    public List<UserDTO> listUserByTeamId(Long teamId) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("id", "team_id");
        List<User> userList = userService.list();
        List<Long> userIdList = userList.stream().map(user -> user.getId()).collect(Collectors.toList());

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.in("user_id", userIdList);
        userInfoQueryWrapper.select("user_id", "user_name", "user_avatar");
        Map<Long, List<UserInfo>> userIdUserInfoMap = userInfoService.list(userInfoQueryWrapper).stream().collect(Collectors.groupingBy(UserInfo::getUserId));

        List<UserDTO> userDTOList = userList.stream().filter(user -> {
            // 获取用户团队 id 列表
            String teamIdStr = user.getTeamId();
            List<Long> teamIdList = gson.fromJson(teamIdStr, new TypeToken<List<Long>>() {
            }.getType());
            return teamIdList.contains(teamId);
        }).map(user -> {
            // user -> userDTO
            UserDTO userDTO = new UserDTO();
            UserInfo userInfo = userIdUserInfoMap.get(user.getId()).get(0);
            userDTO.setId(user.getId());
            userDTO.setUserName(userInfo.getUserName());
            userDTO.setUserAvatar(userInfo.getUserAvatar());
            return userDTO;
        }).collect(Collectors.toList());

        return userDTOList;
    }

    @Override
    public Page<UserVDTO> searchFromEs(UserQueryRequest userQueryRequest) {
        // todo: 从 ES 中搜索数据，并需要从数据库中储存数据
        // ES 中只存储需要搜索的字段
        String searchText = userQueryRequest.getSearchText();

        // es 起始页为 0
        long current = userQueryRequest.getCurrent() - 1;
        long pageSize = userQueryRequest.getPageSize();

        // 获取排序字段
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        // 构建 bool 查询器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 状态正常的
        boolQueryBuilder.filter(QueryBuilders.termQuery("userState", 0));

        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("userName", searchText));;
            boolQueryBuilder.minimumShouldMatch(1);
        }

        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);

        // 构造查询（高亮查询）
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .withHighlightFields(new HighlightBuilder.Field("userName")).build();

        SearchHits<UserEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, UserEsDTO.class);

        // 构造 Page 对象
        Page<UserVDTO> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        page.setCurrent(current);
        page.setSize(pageSize);

        List<UserInfo> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取更多信息
        if (searchHits.hasSearchHits()) {
            List<SearchHit<UserEsDTO>> searchHitList = searchHits.getSearchHits();

            // 构建 ES (id, userName) 的 map
            HashMap<Long, String> userInfoIdNameMap = new HashMap<>();

            searchHitList.stream().forEach(searchHit ->
                    userInfoIdNameMap.put(searchHit.getContent().getId(), searchHit.getHighlightField("userName").get(0)));

            // todo：注意 sortField 若为 all，则表示综合排序
            // 从数据库中取出更完整的数据（并排序）
            QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
            userInfoQueryWrapper.in("id", userInfoIdNameMap.keySet());
            if ("all".equals(sortField)) {
                userInfoQueryWrapper.last("order by 3*like_num+3*view_count+4*follow_count desc");
            } else {
                userInfoQueryWrapper.orderBy(true, CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
            }
            List<UserInfo> userInfoList = userInfoMapper.selectList(userInfoQueryWrapper);

            if (userInfoList != null) {
                // 将数据库中的用户列表 -> （用户 id，用户列表）
                Map<Long, List<UserInfo>> idTeamMap = userInfoList.stream().collect(Collectors.groupingBy(UserInfo::getId));

                // 遍历 ES 中的用户 id 列表，剔除数据库已经不存在的用户
                userInfoIdNameMap.entrySet().forEach(entry -> {
                    Long userInfoId = entry.getKey();
                    String highLightTeamName = entry.getValue();
                    if (idTeamMap.containsKey(userInfoId)) {
                        // 将 team 的非高亮字段赋值为高亮的值
                        UserInfo userInfo = idTeamMap.get(userInfoId).get(0);
                        userInfo.setUserName(highLightTeamName);
                        resourceList.add(userInfo);
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(userInfoId), UserEsDTO.class);
                        log.info("delete user {}", delete);
                    }
                });
            }
        }

        // 将 userinfo 转化成 userDTO List
        List<UserVDTO> userVDTOList = resourceList.stream().map(userInfo -> {
            UserVDTO userVDTO = new UserVDTO();
            BeanUtils.copyProperties(userInfo, userVDTO);
            return userVDTO;
        }).collect(Collectors.toList());

        // 设置记录值
        page.setRecords(userVDTOList);

        return page;
    }

    @Override
    public boolean updateToken(Long userId, Integer token, boolean isAdd) {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", userId);

        // 查询已有 token
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id", userId);
        userQueryWrapper.select("token");
        User user = userService.getOne(userQueryWrapper);
        Integer oldToken = user.getToken();

        // 增加代币
        if (isAdd) {
            userUpdateWrapper.set("token", oldToken + token);
            return userService.update(userUpdateWrapper);
        }

        // 代币不足
        if (oldToken <= token) {
            return false;
        }

        // 消耗代币
        userUpdateWrapper.set("token", oldToken-token);
        return userService.update(userUpdateWrapper);
    }



    @Override
    public boolean addHistory(Long userId, Integer historyObjType, Long historyObjId) {
        History history = new History();
        history.setUserId(userId);history.setHistoryObjType(historyObjType);history.setHistoryObjId(historyObjId);
        historyService.validHistory(history,true);
        History addHistory = historyService.addHistory(history);
        if(addHistory!=null){
            return true;
        }else {
            return false;
        }
    }
}
