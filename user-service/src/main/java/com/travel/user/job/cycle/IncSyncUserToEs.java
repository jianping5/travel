package com.travel.user.job.cycle;

import com.travel.user.esdao.UserEsDao;
import com.travel.user.mapper.UserInfoMapper;
import com.travel.user.model.dto.UserEsDTO;
import com.travel.user.model.entity.UserInfo;
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
public class IncSyncUserToEs {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserEsDao userEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(System.currentTimeMillis() - 5 * 60 * 1000L);
        List<UserInfo> userInfoList = userInfoMapper.listUserWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(userInfoList)) {
            log.info("no inc user");
            return;
        }
        // 将 user 转为 UserEsDTO
        List<UserEsDTO> userEsDTOList = userInfoList.stream()
                .map(UserEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = userEsDTOList.size();
        log.info("IncSyncUserToEs start, total {}", total);

        // 分块添加到 ES 中
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            userEsDao.saveAll(userEsDTOList.subList(i, end));
        }
        log.info("IncSyncUserToEs end, total {}", total);
    }
}
