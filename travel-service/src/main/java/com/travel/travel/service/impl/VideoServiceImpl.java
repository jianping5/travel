package com.travel.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.BehaviorTypeConstant;
import com.travel.common.constant.TypeConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.UserHolder;
import com.travel.travel.model.entity.Article;
import com.travel.travel.model.entity.ArticleDetail;
import com.travel.travel.model.vo.ArticleVO;
import com.travel.travel.model.vo.VideoVO;
import com.travel.travel.model.entity.Video;
import com.travel.travel.model.request.VideoQueryRequest;
import com.travel.travel.service.VideoService;
import com.travel.travel.mapper.VideoMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【video(视频表)】的数据库操作Service实现
* @createDate 2023-04-04 17:31:22
*/
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video>
    implements VideoService, com.travel.common.service.VideoService {

    @DubboReference
    private InnerUserService innerUserService;

    @Resource()
    private RedissonClient redissonClient;
    
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void validVideo(Video video, boolean add) {
        if (video == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = video.getUserId();
        String intro = video.getIntro();
        String coverUrl = video.getCoverUrl();
        String videoUrl = video.getVideoUrl();
        String tag = video.getTag();
        String location = video.getLocation();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(coverUrl,location,videoUrl,tag,intro), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.anyNull(userId,intro,coverUrl,videoUrl,tag,location),ErrorCode.PARAMS_ERROR);
        }
    }

    @Override
    public VideoVO getVideoDetail(Video video) {
        // 获取官方视图体
        VideoVO videoVO = VideoVO.objToVo(video);
        //查询用户信息
        Long userId = video.getUserId();
        UserDTO user = innerUserService.getUser(userId);
        if(user!=null){
            videoVO.setUserName(user.getUserName());
            videoVO.setUserAvatar(user.getUserAvatar());
        }else {
            videoVO.setUserName("用户不存在");
            videoVO.setUserAvatar("");
        }
        //获取当前登录对象
        User loginUser = UserHolder.getUser();
        if(user!=null){
            Long loginUserId = loginUser.getId();

            // 插入历史记录
            String historyMsg = loginUserId+","+ TypeConstant.VIDEO.getTypeIndex()+","+video.getId();
            rabbitTemplate.convertAndSend("user.history.execute",historyMsg);

            // 加入用户行为记录表（消息队列）
            String behaviorMsg = loginUserId+","+TypeConstant.VIDEO.getTypeIndex()+","+video.getId()+","+ BehaviorTypeConstant.VIEW.getTypeIndex();
            rabbitTemplate.convertAndSend("user.behavior.execute",behaviorMsg);

            // 注入点赞状态
            RSet<Long> likeSet = redissonClient.getSet("travel:user:like:" + TypeConstant.VIDEO + ":" + video.getId());
            videoVO.setIsLiked(likeSet.contains(loginUserId)?1:0);

            // 注入收藏状态
            RSet<Long> collectSet = redissonClient.getSet("travel:user:collection:" + TypeConstant.VIDEO + ":" + video.getId());
            videoVO.setIsCollected(collectSet.contains(loginUserId)?1:0);

            // 注入关注状态
            RSet<Long> followSet = redissonClient.getSet("travel:user:follow:"+ video.getUserId());
            videoVO.setIsFollowed(followSet.contains(loginUserId)?1:0);
        }
        return videoVO;
    }

    @Override
    public Page<VideoVO> getVideoVOPage(Page<Video> videoPage) {
        if(videoPage == null){
            return null;
        }
        List<Video> videoList = videoPage.getRecords();
        Page<VideoVO> videoVOPage = new Page<>(videoPage.getCurrent(), videoPage.getSize(), videoPage.getTotal());
        if (CollectionUtils.isEmpty(videoList)) {
            return videoVOPage;
        }

        // 填充信息
        List<VideoVO> videoVOList = getVideoVOList(videoList);
        videoVOPage.setRecords(videoVOList);
        return videoVOPage;
    }

    /**
     * 根据文章列表获取文章视图体列表
     * @param videoList
     * @return
     */
    public List<VideoVO> getVideoVOList(List<Video> videoList) {
        // 获取用户 id 列表
        Set<Long> userIdSet = videoList.stream().map(Video::getUserId).collect(Collectors.toSet());

        // 获取用户列表
        List<UserDTO> userDTOList = innerUserService.listByIds(userIdSet);

        // 将用户 id 和用户对应起来
        Map<Long, List<UserDTO>> userIdUserListMap = userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getId));

        // 填充信息
        List<VideoVO> videoVOList = videoList.stream().map(video -> {
            // 获取官方视图体
            VideoVO videoVO = VideoVO.objToVo(video);
            // 注入用户到官方视图体内
            Long userId = video.getUserId();
            UserDTO userDTO = null;
            if (userIdUserListMap.containsKey(userId)) {
                userDTO = userIdUserListMap.get(userId).get(0);
            }
            videoVO.setUserAvatar(userDTO.getUserAvatar());
            videoVO.setUserName(userDTO.getUserName());

            //获取当前登录对象
            User loginUser = UserHolder.getUser();
            if(loginUser!=null){
                Long loginUserId = loginUser.getId();

                // 注入点赞状态
                RSet<Long> likeSet = redissonClient.getSet("travel:user:like:" + TypeConstant.VIDEO + ":" + video.getId());
                videoVO.setIsLiked(likeSet.contains(loginUserId)?1:0);

                // 注入收藏状态
                RSet<Long> collectSet = redissonClient.getSet("travel:user:collection:" + TypeConstant.VIDEO + ":" + video.getId());
                videoVO.setIsCollected(collectSet.contains(loginUserId)?1:0);

                // 注入关注状态
                RSet<Long> followSet = redissonClient.getSet("travel:user:follow:"+ video.getUserId());
                videoVO.setIsFollowed(followSet.contains(loginUserId)?1:0);
            }

            return videoVO;
        }).collect(Collectors.toList());

        return videoVOList;
    }

    @Override
    public Page<Video> queryVideo(VideoQueryRequest videoQueryRequest) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        if (videoQueryRequest == null) {
            return null;
        }
        // 获取查询条件中的字段
        List<Long> idList = videoQueryRequest.getIdList();
        String searchText = videoQueryRequest.getSearchText();
        Long userId = videoQueryRequest.getUserId();
        Integer permission = videoQueryRequest.getPermission();
        Long teamId = videoQueryRequest.getTeamId();
        Integer queryType = videoQueryRequest.getQueryType();
        Long current = videoQueryRequest.getCurrent();
        Integer videoState = videoQueryRequest.getVideoState();
        Long pageSize = videoQueryRequest.getPageSize();
        Integer orderType = videoQueryRequest.getOrderType();
        //id限制
        if (!ObjectUtils.anyNull(idList)) {
            queryWrapper.in(ObjectUtils.isNotEmpty(idList),"id", idList);
        }
        //内容模糊限制
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(i->i.like("intro", searchText).or().like("title", searchText));
        }
        //文章状态限制
        queryWrapper.eq(ObjectUtils.isNotEmpty(videoState),"video_state", videoState);
        //发布者限制
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        //权限限制
        queryWrapper.eq(ObjectUtils.isNotEmpty(permission), "permission", permission);
        //所属团队限制
        queryWrapper.eq(ObjectUtils.isNotEmpty(teamId), "team_id", teamId);
        //分类限制
        if(queryType!=null&&queryType.equals(0)){
            //旅行游记
            queryWrapper.like("tag","旅行");

        }else if(queryType!=null&&queryType.equals(1)){
            //官方游记
            queryWrapper.like("tag","官方");

        }else if (queryType!=null&&queryType.equals(2)){
            //旅游攻略
            queryWrapper.like("tag","攻略");

        }
        //排序限制
        if(orderType!=null&&orderType.equals(0)){
            //热门推荐
            queryWrapper.last("order by 0.3*like_count + 0.3 * comment_count + 0.3*favorite_count + 0.1*view_count desc");

        }else if(orderType!=null&&orderType.equals(1)){
            //最新发布
            queryWrapper.orderByDesc("update_time");

        }
        return page(new Page<>(current, pageSize), queryWrapper);
    }

    @Override
    public Video addVideo(Video video) {
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        // 设置用户 id
        video.setUserId(loginUserId);

        //todo:考虑事务
        // 添加到数据库中
        boolean saveResult = this.save(video);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);
        // 获取该文章
        Video newVideo = this.getById(video.getId());
        return newVideo;
    }

    @Override
    public boolean deleteVideo(Video video) {
        // 将文章游记状态设置为已下架
        video.setVideoState(2);
        boolean result = this.updateById(video);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean updateVideo(Video video) {

        // 判断当前用户是否为当前文章游记的创建人
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long videoUserId = video.getUserId();
        ThrowUtils.throwIf(!loginUserId.equals(videoUserId), ErrorCode.NO_AUTH_ERROR);

        // 更新数据库
        boolean updateResult = this.updateById(video);
        //todo:加事务
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);
        return true;
    }

}




