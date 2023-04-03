package com.travel.team.job.cycle;

import com.travel.team.esdao.TeamEsDao;
import com.travel.team.mapper.TeamMapper;
import com.travel.team.model.dto.team.TeamEsDTO;
import com.travel.team.model.entity.Team;
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
public class IncSyncPostToEs {

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private TeamEsDao teamEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(System.currentTimeMillis() - 5 * 60 * 1000L);
        List<Team> teamList = teamMapper.listTeamWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(teamList)) {
            log.info("no inc team");
            return;
        }
        // 将 team 转为 TeamEsDTO
        List<TeamEsDTO> teamEsDTOList = teamList.stream()
                .map(TeamEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = teamEsDTOList.size();
        log.info("IncSyncTeamToEs start, total {}", total);

        // 分块添加到 ES 中
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            teamEsDao.saveAll(teamEsDTOList.subList(i, end));
        }
        log.info("IncSyncTeamToEs end, total {}", total);
    }
}
