package com.travel.official.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.BehaviorTypeConstant;
import com.travel.common.constant.CommonConstant;
import com.travel.common.constant.TypeConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.TagAddRequest;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.official.mapper.OfficialMapper;
import com.travel.official.model.dto.official.OfficialQueryRequest;
import com.travel.official.model.entity.Official;
import com.travel.official.model.entity.OfficialDetail;
import com.travel.official.model.vo.OfficialVO;
import com.travel.official.service.OfficialDetailService;
import com.travel.official.service.OfficialService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【official(官方表)】的数据库操作Service实现
* @createDate 2023-03-22 14:45:55
*/
@Service
public class OfficialServiceImpl extends ServiceImpl<OfficialMapper, Official>
    implements OfficialService{

    @Resource
    private OfficialDetailService officialDetailService;

    @Resource
    private RedissonClient redissonClient;

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private Gson gson;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void validOfficial(Official official, boolean add) {
        if (official == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取官方详情
        String detail = official.getDetail();


        if (add) {
            // 官方详情不能为空
            ThrowUtils.throwIf(StringUtils.isAnyBlank(detail), ErrorCode.PARAMS_ERROR);
        }


        // todo: 更详细的验证（相当于发布前进行一个审核？）
    }

    @Override
    public Long addOfficial(Official official) {
        // 完善对应官方内容

        // todo：暂时随机注入浏览量
        official.setViewCount(RandomUtils.nextInt());
        boolean update = this.updateById(official);
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);

        // 赋值给 官方详情（由于参数名不一致，需要手动赋值）
        OfficialDetail officialDetail = new OfficialDetail();
        officialDetail.setDetail(official.getDetail());
        officialDetail.setOfficialId(official.getId());

        // 添加官方详情
        boolean addResult = officialDetailService.addOfficialDetail(officialDetail);
        ThrowUtils.throwIf(!addResult, ErrorCode.OPERATION_ERROR);

        // 添加标签（消息队列）
        TagAddRequest tagAddRequest = new TagAddRequest();
        tagAddRequest.setTagList(official.getTag());
        tagAddRequest.setTagType(TypeConstant.OFFICIAL.getTypeIndex());
        String tagAddRequestJson = gson.toJson(tagAddRequest);
        String exchangeName = "travel.topic";
        // todo：如何确保消息正确地被消费？
        rabbitTemplate.convertAndSend(exchangeName, "tag.official", tagAddRequestJson);

        return official.getId();
    }

    @Override
    public boolean updateOfficial(Official official) {
        // 判断当前用户是否为当前官方的创建人
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long officialUserId = official.getUserId();
        ThrowUtils.throwIf(!loginUserId.equals(officialUserId), ErrorCode.NO_AUTH_ERROR);

        // 赋值给 官方详情（由于参数名不一致，需要手动赋值）
        OfficialDetail officialDetail = new OfficialDetail();
        officialDetail.setDetail(official.getDetail());
        officialDetail.setOfficialId(official.getId());
        officialDetail.setId(official.getOfficialDetailId());

        // 更新官方详情
        officialDetailService.updateOfficialDetail(officialDetail);

        // 更新官方
        boolean updateResult = this.updateById(official);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public OfficialVO getOfficialVO(Official official) {
        OfficialVO officialVO = OfficialVO.objToVo(official);

        // 关联查询用户信息
        Long userId = official.getUserId();
        UserDTO userDTO = null;
        if (userId != null && userId > 0) {
            userDTO = innerUserService.getUser(userId);
        }
        officialVO.setUser(userDTO);

        // 关联官方详情的 id
        Long officialId = official.getId();
        QueryWrapper<OfficialDetail> officialDetailQueryWrapper = new QueryWrapper<>();
        officialDetailQueryWrapper.eq("official_id", officialId);
        OfficialDetail officialDetail = officialDetailService.getOne(officialDetailQueryWrapper);
        officialVO.setOfficialDetailId(officialDetail.getId());

        return officialVO;
    }

    @Override
    public OfficialVO getOfficialDetail(Official official) {
        OfficialVO officialVO = OfficialVO.objToVo(official);

        // 关联查询用户信息
        Long userId = official.getUserId();
        UserDTO userDTO = null;
        if (userId != null && userId > 0) {
            userDTO = innerUserService.getUser(userId);
        }
        officialVO.setUser(userDTO);

        // 获取官方详情 id
        Long officialDetailId = official.getOfficialDetailId();

        // 获取官方详情
        OfficialDetail officialDetail = officialDetailService.getById(officialDetailId);

        // 注入到官方详情视图体中
        officialVO.setOfficialDetailId(officialDetail.getId());
        officialVO.setDetail(officialDetail.getDetail());

        // 若已登录，点赞
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        // 是否点赞
        String officialLike = String.format("travel:official:like:%d:%d", TypeConstant.OFFICIAL.getTypeIndex(), official.getId());
        RSet<Long> officialLikeSet = redissonClient.getSet(officialLike);
        if (officialLikeSet.contains(loginUserId)) {
            officialVO.setIsLiked(1);
        } else {
            officialVO.setIsLiked(0);
        }

        // 将当前用户查看当前官方详情加入用户行为记录表（消息队列）
        String exchangeName = "travel.topic";
        // 定义用户行为消息
        String behaviorMessage = loginUserId + " " + TypeConstant.OFFICIAL.getTypeIndex() + " " + official.getId() + " " + BehaviorTypeConstant.LIKE.getTypeName();
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "behavior.official.view", behaviorMessage);


        return officialVO;
    }

    @Override
    public Page<OfficialVO> getOfficialVOPage(Page<Official> officialPage) {
        List<Official> officialList = officialPage.getRecords();
        Page<OfficialVO> officialVOPage = new Page<>(officialPage.getCurrent(), officialPage.getSize(), officialPage.getTotal());
        if (CollectionUtils.isEmpty(officialList)) {
            return officialVOPage;
        }

        // 根据官方列表获取官方视图体列表
        List<OfficialVO> officialVOList = getOfficialVOList(officialList);

        officialVOPage.setRecords(officialVOList);
        return officialVOPage;
    }

    @Override
    public QueryWrapper<Official> getQueryWrapper(OfficialQueryRequest officialQueryRequest) {
        QueryWrapper<Official> queryWrapper = new QueryWrapper<>();
        if (officialQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询条件中的字段
        String searchText = officialQueryRequest.getSearchText();
        String sortField = officialQueryRequest.getSortField();
        String sortOrder = officialQueryRequest.getSortOrder();
        Long id = officialQueryRequest.getId();
        String officialName = officialQueryRequest.getOfficialName();
        Long userId = officialQueryRequest.getUserId();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("official_name", searchText).or().like("intro", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(officialName), "official_name", officialName);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public List<OfficialVO> listRcmdOfficialVO(long current, long size, int typeId) {
        // 创建官方视图体数组
        List<OfficialVO> officialVOList = new ArrayList<>();

        // 判断有无缓存
        RList<String> officialVORList = redissonClient.getList("travel:official:recommend:" + typeId);

        // 若有，则直接从缓存中读取
        if (CollectionUtils.isNotEmpty(officialVORList)) {
            for (long i = (current-1)*size; i < current*size; i++) {
                String json = officialVORList.get((int) i);
                OfficialVO officialVO = gson.fromJson(json, OfficialVO.class);
                officialVOList.add(officialVO);
            }
            return officialVOList;
        }

        // 若无，则从数据库中读取
        QueryWrapper<Official> officialQueryWrapper = new QueryWrapper<>();
        officialQueryWrapper.eq("type_id", typeId);
        officialQueryWrapper.last("order by 5*like_count+3*favorite_count+view_count+review_count desc limit " + size);

        // 根据官方列表获取官方视图体列表
        List<Official> officialList = this.list(officialQueryWrapper);
        officialVOList = getOfficialVOList(officialList);

        // todo: 并将写缓存的任务添加到消息队列
        // 定义交换机名称
        String exchangeName = "travel.topic";

        // 定义消息
        String message = String.valueOf(typeId);

        // 发送消息，让对应线程将数据写入缓存
        rabbitTemplate.convertAndSend(exchangeName, "cache.official", message);

        return officialVOList;
    }

    /**
     * 根据官方列表获取官方视图体列表
     * @param officialList
     * @return
     */
    @Override
    public List<OfficialVO> getOfficialVOList(List<Official> officialList) {
        // 获取用户 id 列表
        Set<Long> userIdSet = officialList.stream().map(Official::getUserId).collect(Collectors.toSet());

        // 获取用户列表
        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);

        // 将用户 id 和用户对应起来
        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));

        // 获取 map（官方 id，官方详情 List）
        Set<Long> officialIdSet = officialList.stream().map(official -> official.getId()).collect(Collectors.toSet());
        QueryWrapper<OfficialDetail> officialDetailQueryWrapper = new QueryWrapper<>();
        officialDetailQueryWrapper.select("id", "official_id");
        officialDetailQueryWrapper.in(CollectionUtils.isNotEmpty(officialIdSet),"official_id", officialIdSet);
        List<OfficialDetail> officialDetailList = officialDetailService.list(officialDetailQueryWrapper);
        Map<Long, List<OfficialDetail>> officialIdDetailListMap = officialDetailList.stream().collect(Collectors.groupingBy(OfficialDetail::getOfficialId));

        // 填充信息
        List<OfficialVO> officialVOList = officialList.stream().map(official -> {
            // 获取官方视图体
            OfficialVO officialVO = OfficialVO.objToVo(official);
            // 注入用户到官方视图体内
            Long userId = official.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            officialVO.setUser(userDTO);

            // 注入官方详情 id 到官方视图体内
            Long officialId = official.getId();
            OfficialDetail officialDetail = null;
            if (officialIdDetailListMap.containsKey(officialId)) {
                officialDetail = officialIdDetailListMap.get(officialId).get(0);
            }
            officialVO.setOfficialDetailId(officialDetail.getId());
            return officialVO;
        }).collect(Collectors.toList());

        return officialVOList;
    }

    @Override
    public boolean likeOfficial(Long id, Integer type, Integer status) {
        // 获取当前登录用户
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();

        // 从 redis 中获取对应的 set
        String cacheKey = String.format("travel:official:like:%d:%d", type, id);
        RSet<Long> set = redissonClient.getSet(cacheKey);

        // 获取对应锁
        RLock lock = redissonClient.getLock("travel:official:like:lock:" + type + ":" +id);

        // 定义交换机名称
        String exchangeName = "travel.topic";
        String message = null;
        String behaviorMessage = null;

        // 分布式锁保证同一时间同一文章只有一个线程可以对其进行点赞/取消点赞操作
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                // 点赞
                if (status == 1) {
                    // 更新文章的对应点赞用户集合
                    set.add(loginUserId);

                    // 将对应实体类型的点赞量 +1，并添加用户的点赞记录到点赞表中（消息队列）
                    // 定义消息
                    message = loginUserId + " " + type + " " + id + " " + status;
                    // 定义用户行为消息
                    behaviorMessage = loginUserId + " " + type + " " + id + " " + BehaviorTypeConstant.LIKE.getTypeName();
                }

                // 取消点赞
                if (status == 2) {
                    // 更新文章的对应点赞用户集合
                    set.remove(loginUserId);

                    // 将对应实体类型的点赞量 -1，并删除用户的点赞记录（消息队列）
                    // 定义消息
                    message = loginUserId + " " + type + " " + id + " " + status;
                    // 定义用户行为消息
                    behaviorMessage = loginUserId + " " + type + " " + id + " " + BehaviorTypeConstant.DISLIKE.getTypeName();
                }

                // 发送消息，让对应线程将数据写入数据库
                rabbitTemplate.convertAndSend(exchangeName, "cache.official.like", message);

                // 发送消息，添加用户行为记录
                rabbitTemplate.convertAndSend(exchangeName, "behavior.official.like", behaviorMessage);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }

        return true;
    }
}






