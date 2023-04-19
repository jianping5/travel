package com.travel.travel.mapper;

import com.travel.travel.model.entity.Video;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
* @author jianping5
* @description 针对表【video(视频表)】的数据库操作Mapper
* @createDate 2023-04-04 17:31:22
* @Entity com.travel.travel.model.entity.Video
*/
public interface VideoMapper extends BaseMapper<Video> {

    /**
     * 查询某段时间的视频列表（包括已被删除的数据）
     * @param minUpdateTime
     * @return
     */
    List<Video> listVideoWithDelete(Date minUpdateTime);
}




