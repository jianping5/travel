package com.travel.official.job.cycle;

import com.travel.official.esdao.OfficialEsDao;
import com.travel.official.mapper.OfficialMapper;
import com.travel.official.model.dto.official.OfficialEsDTO;
import com.travel.official.model.entity.Official;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步团队到 es
 *
 * @author jianping5
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class IncSyncOfficialToEs {

    @Resource
    private OfficialMapper officialMapper;

    @Resource
    private OfficialEsDao officialEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(System.currentTimeMillis() - 5 * 60 * 1000L);
        List<Official> officialList = officialMapper.listOfficialWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(officialList)) {
            log.info("no inc official");
            return;
        }
        // 将 official 转为 OfficialEsDTO
        List<OfficialEsDTO> officialEsDTOList = officialList.stream()
                .map(OfficialEsDTO::objToDto)
                .collect(Collectors.toList());

        final int pageSize = 500;
        int total = officialEsDTOList.size();
        log.info("IncSyncOfficialToEs start, total {}", total);

        // 分块添加到 ES 中
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            officialEsDao.saveAll(officialEsDTOList.subList(i, end));
        }
        log.info("IncSyncOfficialToEs end, total {}", total);
    }
}
