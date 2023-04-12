package com.travel.official.job.once;

import com.travel.official.esdao.DerivativeEsDao;
import com.travel.official.model.dto.derivative.DerivativeEsDTO;
import com.travel.official.model.entity.Derivative;
import com.travel.official.service.DerivativeService;
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
public class FullSyncDerivativeToEs implements CommandLineRunner {

    @Resource
    private DerivativeService derivativeService;

    @Resource
    private DerivativeEsDao derivativeEsDao;

    @Override
    public void run(String... args) {
        List<Derivative> derivativeList = derivativeService.list();
        if (CollectionUtils.isEmpty(derivativeList)) {
            return;
        }
        List<DerivativeEsDTO> derivativeEsDTOList = derivativeList.stream().map(DerivativeEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = derivativeEsDTOList.size();
        log.info("FullSyncDerivativeToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            derivativeEsDao.saveAll(derivativeEsDTOList.subList(i, end));
        }
        log.info("FullSyncDerivativeToEs end, total {}", total);
    }
}
