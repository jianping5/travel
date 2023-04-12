package com.travel.official.job.cycle;

import com.travel.official.esdao.DerivativeEsDao;
import com.travel.official.mapper.DerivativeMapper;
import com.travel.official.model.dto.derivative.DerivativeEsDTO;
import com.travel.official.model.entity.Derivative;
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
public class IncSyncDerivativeToEs {

    @Resource
    private DerivativeMapper derivativeMapper;

    @Resource
    private DerivativeEsDao derivativeEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(System.currentTimeMillis() - 5 * 60 * 1000L);
        // 将删除的数据也需要查出来，然后增量同步到 ES 中（因为 ES 中若存在该数据，则会执行更新操作，将其状态更新为已删除）
        List<Derivative> derivativeList = derivativeMapper.listDerivativeWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(derivativeList)) {
            log.info("no inc derivative");
            return;
        }
        // 将 derivative 转为 DerivativeEsDTO
        List<DerivativeEsDTO> derivativeEsDTOList = derivativeList.stream()
                .map(DerivativeEsDTO::objToDto)
                .collect(Collectors.toList());

        final int pageSize = 500;
        int total = derivativeEsDTOList.size();
        log.info("IncSyncDerivativeToEs start, total {}", total);

        // 分块添加到 ES 中
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            derivativeEsDao.saveAll(derivativeEsDTOList.subList(i, end));
        }
        log.info("IncSyncDerivativeToEs end, total {}", total);
    }
}
