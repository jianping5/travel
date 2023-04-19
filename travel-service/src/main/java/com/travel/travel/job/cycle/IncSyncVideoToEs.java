package com.travel.travel.job.cycle;


import com.travel.travel.esdao.VideoEsDao;
import com.travel.travel.mapper.VideoMapper;
import com.travel.travel.model.dto.VideoEsDTO;
import com.travel.travel.model.entity.Video;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步周边到 es
 *
 * @author jianping5
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class IncSyncVideoToEs {

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private VideoEsDao videoEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(System.currentTimeMillis() - 5 * 60 * 1000L);
        // 将删除的数据也需要查出来，然后增量同步到 ES 中（因为 ES 中若存在该数据，则会执行更新操作，将其状态更新为已删除）
        List<Video> videoList = videoMapper.listVideoWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(videoList)) {
            log.info("no inc video");
            return;
        }
        // 将 video 转为 VideoEsDTO
        List<VideoEsDTO> videoEsDTOList = videoList.stream()
                .map(VideoEsDTO::objToDto)
                .collect(Collectors.toList());

        final int pageSize = 500;
        int total = videoEsDTOList.size();
        log.info("IncSyncVideoToEs start, total {}", total);

        // 分块添加到 ES 中
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            videoEsDao.saveAll(videoEsDTOList.subList(i, end));
        }
        log.info("IncSyncVideoToEs end, total {}", total);
    }
}
