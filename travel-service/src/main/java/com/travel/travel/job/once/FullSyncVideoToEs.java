package com.travel.travel.job.once;

import com.travel.travel.esdao.VideoEsDao;
import com.travel.travel.model.dto.VideoEsDTO;
import com.travel.travel.model.entity.Video;
import com.travel.travel.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步团队到 es
 *
 * @author jianping5
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class FullSyncVideoToEs implements CommandLineRunner {

    @Resource
    private VideoService videoService;

    @Resource
    private VideoEsDao videoEsDao;

    @Override
    public void run(String... args) {
        List<Video> videoList = videoService.list();
        if (CollectionUtils.isEmpty(videoList)) {
            return;
        }
        List<VideoEsDTO> videoEsDTOList = videoList.stream().map(VideoEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = videoEsDTOList.size();
        log.info("FullSyncVideoToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            videoEsDao.saveAll(videoEsDTOList.subList(i, end));
        }
        log.info("FullSyncVideoToEs end, total {}", total);
    }
}
