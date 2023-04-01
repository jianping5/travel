package com.travel.team.job.once;

import com.travel.team.esdao.TeamEsDao;
import com.travel.team.model.dto.team.TeamEsDTO;
import com.travel.team.model.entity.Team;
import com.travel.team.service.TeamService;
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
public class FullSyncTeamToEs implements CommandLineRunner {

    @Resource
    private TeamService teamService;

    @Resource
    private TeamEsDao teamEsDao;

    @Override
    public void run(String... args) {
        List<Team> teamList = teamService.list();
        if (CollectionUtils.isEmpty(teamList)) {
            return;
        }
        List<TeamEsDTO> teamEsDTOList = teamList.stream().map(TeamEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = teamEsDTOList.size();
        log.info("FullSyncTeamToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            teamEsDao.saveAll(teamEsDTOList.subList(i, end));
        }
        log.info("FullSyncTeamToEs end, total {}", total);
    }
}
