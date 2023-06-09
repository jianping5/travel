package com.travel.team.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.constant.TypeConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.MessageDTO;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerTravelService;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.RegexUtils;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.team.mapper.TeamMapper;
import com.travel.team.mapper.TeamNewsMapper;
import com.travel.team.mapper.TeamWallMapper;
import com.travel.team.model.dto.team.TeamQueryRequest;
import com.travel.team.model.entity.Team;
import com.travel.team.model.entity.TeamApply;
import com.travel.team.model.entity.TeamNews;
import com.travel.team.model.entity.TeamWall;
import com.travel.team.model.vo.TeamVO;
import com.travel.team.service.TeamApplyService;
import com.travel.team.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【team(团队表)】的数据库操作Service实现
* @createDate 2023-03-22 14:40:38
*/
@Slf4j
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerTravelService innerTravelService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private TeamNewsMapper teamNewsMapper;

    @Resource
    private TeamWallMapper teamWallMapper;

    @Resource
    private TeamApplyService teamApplyService;

    @Resource
    private Gson gson;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void validTeam(Team team, boolean add) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String teamName = team.getTeamName();
        String intro = team.getIntro();
        // todo: 这两者可以考虑使用默认值（若用户不传）
        String coverUrl = team.getCoverUrl();
        String iconUrl = team.getIconUrl();
        if (StringUtils.isEmpty(coverUrl)) {
            coverUrl = "https://p1-q.mafengwo.net/s19/M00/B2/3C/CoND0WQMEK8k0egOAACDIIkUhqg.jpeg?imageMogr2%2Fthumbnail%2F%21200x200r%2Fgravity%2FCenter%2Fcrop%2F%21200x200%2Fquality%2F90";
            team.setCoverUrl(coverUrl);
        }
        if (StringUtils.isEmpty(iconUrl)) {
            iconUrl = "https://p1-q.mafengwo.net/s19/M00/B2/3C/CoND0WQMEK8k0egOAACDIIkUhqg.jpeg?imageMogr2%2Fthumbnail%2F%21200x200r%2Fgravity%2FCenter%2Fcrop%2F%21200x200%2Fquality%2F90";
            team.setIconUrl(iconUrl);
        }

        // 校验 URL 是否合法
        boolean urlValid = RegexUtils.isUrlValid(coverUrl);
        boolean urlValid1 = RegexUtils.isUrlValid(iconUrl);
        ThrowUtils.throwIf(!urlValid || !urlValid1, ErrorCode.PARAMS_ERROR);

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(teamName), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(teamName) && teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "团队名称过长");
        }
        if (StringUtils.isNotBlank(intro) && intro.length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "团队简介过长");
        }
    }

    @Override
    public TeamVO getTeamVO(Team team) {
        TeamVO teamVO = TeamVO.objToVo(team);

        // 1. 关联查询用户信息
        Long userId = team.getUserId();
        UserDTO userDTO = null;
        if (userId != null && userId > 0) {
            userDTO = innerUserService.getUser(userId);
        }
        teamVO.setUser(userDTO);

        return teamVO;
    }

    @Override
    public Page<TeamVO> getTeamVOPage(Page<Team> teamPage) {
        List<Team> teamList = teamPage.getRecords();
        Page<TeamVO> teamVOPage = new Page<>(teamPage.getCurrent(), teamPage.getSize(), teamPage.getTotal());
        if (CollectionUtils.isEmpty(teamList)) {
            return teamVOPage;
        }

        // 根据团队列表获取团队视图体列表
        List<TeamVO> teamVOList = getTeamVOList(teamList);

        teamVOPage.setRecords(teamVOList);

        return teamVOPage;
    }

    @Override
    public QueryWrapper<Team> getQueryWrapper(TeamQueryRequest teamQueryRequest) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询条件中的字段
        String searchText = teamQueryRequest.getSearchText();
        String sortField = teamQueryRequest.getSortField();
        String sortOrder = teamQueryRequest.getSortOrder();
        Long id = teamQueryRequest.getId();
        String teamName = teamQueryRequest.getTeamName();
        String intro = teamQueryRequest.getIntro();
        Long userId = teamQueryRequest.getUserId();
        Integer teamState = teamQueryRequest.getTeamState();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("team_name", searchText).or().like("intro", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(teamName), "team_name", teamName);
        queryWrapper.like(StringUtils.isNotBlank(intro), "intro", intro);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        queryWrapper.eq(ObjectUtils.isNotEmpty(teamState), "team_state", 0);
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Team addTeam(Team team) {
        // todo: 判重（团队名字）是否需要？

        // 设置 team 的创建人
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        team.setUserId(loginUserId);

        // todo：判断当前用户创建团队数是否达到 5，若已经达到 5，则需要花费代币


        // todo：将代币消费记录添加到奖励服务中用户的消费记录（记录团队创建）

        // 添加到数据库中
        boolean saveResult = this.save(team);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);

        // 获取该 team
        Team newTeam = this.getById(team.getId());

        return newTeam;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Team team) {
        // todo: 考虑加事务，team_id 是否需要加索引

        // 将团队状态设置为已解散
        team.setTeamState(2);
        this.updateById(team);

        // 删除该团队内所发布的全部动态、全部团队墙
        QueryWrapper<TeamNews> teamNewsQueryWrapper = new QueryWrapper<>();
        teamNewsQueryWrapper.eq("team_id", team.getId());
        teamNewsMapper.delete(teamNewsQueryWrapper);

        QueryWrapper<TeamWall> teamWallQueryWrapper = new QueryWrapper<>();
        teamWallQueryWrapper.eq("team_id", team.getId());
        teamWallMapper.delete(teamWallQueryWrapper);

        // todo：将解散消息发给每个关联用户（用户信息，团队信息，解散信息）


        // 将原有团队的所有个人游记的团队字段改为团队回收站的团队 id
        innerTravelService.updateTravelByTeamId(null, team.getId());

        return true;
    }

    @Override
    public boolean updateTeam(Team team) {
        // 查询是否存在团队
        Long teamId = team.getId();
        Team oldTeam = this.getById(teamId);
        ThrowUtils.throwIf(oldTeam == null, ErrorCode.NOT_FOUND_ERROR);

        // 判断当前用户是否为当前团队的创始人
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long teamUserId = oldTeam.getUserId();
        ThrowUtils.throwIf(!loginUserId.equals(teamUserId), ErrorCode.NO_AUTH_ERROR);

        // 更新数据库
        boolean updateResult = this.updateById(team);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);

        return true;
    }

    @Override
    public boolean changeTeam(Long userId, Long teamId, Integer joinOrQuitOrKick) {

        // 根据用户 id 从用户表中查询，判断用户是否已经加入该团队
        boolean joined = innerUserService.isJoined(userId, teamId);

        // 根据团队 id 从团队表中查询团队
        Team team = this.getById(teamId);

        // 加入团队
        if (joinOrQuitOrKick == 0) {

            // 判断团队容量是否已满
            Integer teamSize = team.getTeamSize();
            Integer capacity = team.getCapacity();
            ThrowUtils.throwIf(teamSize > capacity, ErrorCode.OPERATION_ERROR, "团队容量已满");

            ThrowUtils.throwIf(joined, ErrorCode.OPERATION_ERROR, "不要重复加入团队");

            // 需要申请
            Integer isAudit = team.getIsAudit();
            if (isAudit == 1) {
                // 加入团队申请表中
                TeamApply teamApply = new TeamApply();
                teamApply.setUserId(userId);
                teamApply.setTeamId(teamId);
                boolean saveResult = teamApplyService.save(teamApply);
                ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);
            }

            // 无需申请
            if (isAudit == 0) {
                // 加入团队
                innerUserService.changeTeam(userId, teamId, 0);

                // 团队人数 +1
                UpdateWrapper<Team> teamUpdateWrapper = new UpdateWrapper<>();
                teamUpdateWrapper.setSql("team_size = team_size + 1");
                boolean updateResult = this.update(teamUpdateWrapper);
                ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);
            }

        }

        // 退出团队
        if (joinOrQuitOrKick == 1) {

            ThrowUtils.throwIf(!joined, ErrorCode.OPERATION_ERROR, "该成员已不属于该团队");

            // 更换团队（用户服务）
            innerUserService.changeTeam(userId, teamId, 2);

            // 团队人数减一
            UpdateWrapper<Team> teamUpdateWrapper = new UpdateWrapper<>();
            teamUpdateWrapper.setSql("team_size = team_size - 1");
            boolean updateResult = this.update(teamUpdateWrapper);
            ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);

            // todo：更换游记的团队 id（游记服务）异常处理？
            try {
                innerTravelService.updateTravelByTeamId(userId, teamId);
            } catch (Exception e) {
                ThrowUtils.throwIf(true, ErrorCode.OPERATION_ERROR);
            }

            // todo：将该成员退出团队的消息告知团队创始人
            // 交换机名称
            String exchangeName = "travel.topic";

            // 消息名称
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setUserId(team.getUserId());
            messageDTO.setTitle("团队成员退出");
            messageDTO.setMessageObjType(TypeConstant.USER.getTypeIndex());
            messageDTO.setMessageObjId(userId);
            messageDTO.setMessageUserId(userId);
            messageDTO.setContent("该用户退出了您的团队（" + team.getTeamName() + "）");

            // todo：启动类，配置了 JSON，还需要使用 gson 序列化再加入队列中吗
            String messageDTOJson = gson.toJson(messageDTO);

            // 发送该消息
            rabbitTemplate.convertAndSend(exchangeName, "message.team", messageDTOJson);

        }

        // 踢出团队
        if (joinOrQuitOrKick == 2) {
            ThrowUtils.throwIf(!joined, ErrorCode.OPERATION_ERROR, "您不属于该团队");

            // 更换团队（用户服务）
            innerUserService.changeTeam(userId, teamId, 1);

            // 团队人数减一
            UpdateWrapper<Team> teamUpdateWrapper = new UpdateWrapper<>();
            teamUpdateWrapper.setSql("team_size = team_size - 1");
            boolean updateResult = this.update(teamUpdateWrapper);
            ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);

            // 更换指定用户游记的团队 id（游记服务）
            innerTravelService.updateTravelByTeamId(userId, teamId);

            // todo：将该成员被踢出团队的消息告知团队创始人
        }

        return true;
    }

    @Override
    public List<Team> listMyTeam(Long userId) {
        // 若未传用户 id，则默认为当前登录用户 id
        if (userId == null) {
            User loginUser = UserHolder.getUser();
            userId = loginUser.getId();
        }

        // 根据用户 id 获取团队 id 列表
        String teamIdStr = innerUserService.getTeamIdStr(userId);
        List<Long> teamIdList = gson.fromJson(teamIdStr, new TypeToken<List<Long>>() {
        }.getType());

        // todo：根据团队 id 列表获取团队名称
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.in(CollectionUtils.isNotEmpty(teamIdList),"id", teamIdList);
        teamQueryWrapper.select("id", "team_name", "team_size", "team_state");
        return this.list(teamQueryWrapper).stream().collect(Collectors.toList());
    }

    @Override
    public List<TeamVO> listRcmdTeamVO(long current, long size) {
        // 创建团队视图体数组
        List<TeamVO> teamVOList = new ArrayList<>();

        // 判断有无缓存
        RList<String> teamVORList = redissonClient.getList("travel:team:recommend");

        // 若有，则直接从缓存中读取
        if (CollectionUtils.isNotEmpty(teamVORList)) {
            for (long i = current; i < size; i++) {
                String json = teamVORList.get((int) i);
                TeamVO teamVO = gson.fromJson(json, TeamVO.class);
                teamVOList.add(teamVO);
            }
            return teamVOList;
        }

        // 若无，则从数据库中读取
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.last("order by 5*travel_count+3*news_count+2*team_size desc limit " + size);

        // 根据团队列表获取团队视图体列表
        List<Team> teamList = this.list(teamQueryWrapper);
        teamVOList = getTeamVOList(teamList);

        // todo: 并将写缓存的任务添加到消息队列
        // 定义交换机名称
        String exchangeName = "travel.topic";

        // 定义消息
        String message = "cache.team";

        // 发送消息，让对应线程将数据写入缓存
        rabbitTemplate.convertAndSend(exchangeName, "cache.team", message);

        return teamVOList;
    }

    @Override
    public List<TeamVO> getTeamVOList(List<Team> teamList) {
        // 1. 关联查询用户信息
        Set<Long> userIdSet = teamList.stream().map(Team::getUserId).collect(Collectors.toSet());
        // 这样做的好处是将 用户 id 和其对应的信息直接对应起来，方便后续获取（而不用遍历获取）

        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);

        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));

        // 填充信息
        List<TeamVO> teamVOList = teamList.stream().map(team -> {
            TeamVO teamVO = TeamVO.objToVo(team);
            Long userId = team.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            teamVO.setUser(userDTO);
            return teamVO;
        }).collect(Collectors.toList());

        return teamVOList;
    }

    @Override
    public List<UserDTO> listTeamMember(Long teamId) {

        return innerUserService.listUserByTeamId(teamId);
    }

    @Override
    public List<Team> listMyCreateTeam(Long id) {

        // 根据用户 id 获取团队 id 列表
        String teamIdStr = innerUserService.getTeamIdStr(id);
        List<Long> teamIdList = gson.fromJson(teamIdStr, new TypeToken<List<Long>>() {
        }.getType());

        // todo：根据团队 id 列表获取团队名称
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.in(CollectionUtils.isNotEmpty(teamIdList),"id", teamIdList);
        teamQueryWrapper.eq("user_id", id);
        teamQueryWrapper.select("id", "team_name", "team_size", "team_state");

        return this.list(teamQueryWrapper).stream().collect(Collectors.toList());
    }
}




