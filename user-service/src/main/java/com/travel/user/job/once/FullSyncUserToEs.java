package com.travel.user.job.once;

import com.travel.user.esdao.UserEsDao;
import com.travel.user.model.dto.UserEsDTO;
import com.travel.user.model.entity.UserInfo;
import com.travel.user.service.UserInfoService;
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
public class FullSyncUserToEs implements CommandLineRunner {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserEsDao userEsDao;

    @Override
    public void run(String... args) {
        List<UserInfo> userInfoList = userInfoService.list();
        if (CollectionUtils.isEmpty(userInfoList)) {
            return;
        }
        List<UserEsDTO> userEsDTOList = userInfoList.stream().map(UserEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = userEsDTOList.size();
        log.info("FullSyncUserToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            userEsDao.saveAll(userEsDTOList.subList(i, end));
        }
        log.info("FullSyncUserToEs end, total {}", total);
    }
}
