package com.travel.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.travel.model.vo.VideoVO;
import com.travel.travel.model.entity.Video;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.travel.model.request.VideoQueryRequest;

/**
* @author jianping5
* @description 针对表【video(视频表)】的数据库操作Service
* @createDate 2023-04-04 17:31:22
*/
public interface VideoService extends IService<Video> {
    /**
     * 校验 Video
     * @param video
     * @param b
     */
    void validVideo(Video video, boolean b);

    /**
     * 获取视频游记视图体
     * @param Video
     * @return
     */
    VideoVO getVideoVO(Video Video);

    /**
     * 获取分页视频游记视图体
     * @param VideoPage
     * @return
     */
    Page<VideoVO> getVideoVOPage(Page<Video> VideoPage);

    /**
     * 根据请求体获取请求 Wrapper
     * @param VideoQueryRequest
     * @return
     */
    Page<Video> queryVideo(VideoQueryRequest VideoQueryRequest);

    /**
     * 创建视频游记
     * @param Video
     * @return
     */
    Video addVideo(Video Video);


    /**
     * 解散视频游记
     * @param Video
     * @return
     */
    boolean deleteVideo(Video Video);

    /**
     * 更新视频游记
     * @param Video
     * @return
     */
    boolean updateVideo(Video Video);

}
