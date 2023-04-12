package com.travel.official.job.once;

import com.travel.official.esdao.OfficialEsDao;
import com.travel.official.model.dto.official.OfficialEsDTO;
import com.travel.official.model.entity.Official;
import com.travel.official.service.OfficialService;
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
public class FullSyncOfficialToEs implements CommandLineRunner {

    @Resource
    private OfficialService officialService;

    @Resource
    private OfficialEsDao officialEsDao;

    @Override
    public void run(String... args) {
        List<Official> officialList = officialService.list();
        if (CollectionUtils.isEmpty(officialList)) {
            return;
        }
        List<OfficialEsDTO> officialEsDTOList = officialList.stream().map(OfficialEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = officialEsDTOList.size();
        log.info("FullSyncOfficialToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            officialEsDao.saveAll(officialEsDTOList.subList(i, end));
        }
        log.info("FullSyncOfficialToEs end, total {}", total);
    }
}
