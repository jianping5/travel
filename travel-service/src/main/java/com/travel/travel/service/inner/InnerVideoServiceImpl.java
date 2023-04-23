package com.travel.travel.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.vo.VideoVDTO;
import com.travel.common.service.InnerVideoService;
import com.travel.travel.model.entity.Video;
import com.travel.travel.model.vo.VideoVO;
import com.travel.travel.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 6:33
 */
@DubboService
@Slf4j
public class InnerVideoServiceImpl implements InnerVideoService {

    @Resource
    private VideoService videoService;


    @Override
    public Page<VideoVDTO> listPersonalRcmd(Set<Long> idList, long pageNum, long pageSize) {
        // todo：查询个性化推荐实体列表
        // 先根据行为对象 id 列表查询对应的用户集
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.select("user_id");
        videoQueryWrapper.in(!ObjectUtils.isEmpty(idList),"id", idList);
        Set<Long> userIdSet = videoService.list(videoQueryWrapper).stream().map(video -> video.getUserId()).collect(Collectors.toSet());

        // 根据用户集查询这些用户最近发布的游记
        QueryWrapper<Video> newVideoQueryWrapper = new QueryWrapper<>();
        newVideoQueryWrapper.in("user_id", userIdSet);
        newVideoQueryWrapper.orderByDesc("create_time");
        Page<Video> page = videoService.page(new Page<>(pageNum, pageSize), newVideoQueryWrapper);

        return videoObjPageToVdtoPage(page);
    }

    /**
     * 将实体 page 转换成 VDTO page
     * @param videoPage
     * @return
     */
    private Page<VideoVDTO> videoObjPageToVdtoPage(Page<Video> videoPage) {
        // videoPage -> videoVDTOPage
        // 将 videoPage -> videoVOPage -> videoVOList
        List<VideoVO> videoVOList = videoService.getVideoVOPage(videoPage).getRecords();

        Page<VideoVDTO> videoVDTOPage = new Page<>(videoPage.getCurrent(), videoPage.getSize());

        // 将 videoVOList -> videoVDTOList
        List<VideoVDTO> videoVDTOList = videoVOList.stream().map(videoVO -> {
            VideoVDTO videoVDTO = new VideoVDTO();
            BeanUtils.copyProperties(videoVO, videoVDTO);
            return videoVDTO;
        }).collect(Collectors.toList());

        // 为 videoVDTOPage 注入属性
        videoVDTOPage.setTotal(videoPage.getTotal());
        videoVDTOPage.setRecords(videoVDTOList);

        return videoVDTOPage;
    }
}
