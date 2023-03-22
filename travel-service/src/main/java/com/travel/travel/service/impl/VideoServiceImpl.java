package com.travel.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.travel.model.entity.Video;
import com.travel.travel.service.VideoService;
import com.travel.travel.mapper.VideoMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【video(视频表)】的数据库操作Service实现
* @createDate 2023-03-22 14:38:42
*/
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video>
    implements VideoService{

}




