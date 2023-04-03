package com.travel.team.model.vo;

import com.travel.common.model.dto.UserDTO;
import com.travel.team.model.entity.TeamNews;
import com.travel.team.model.entity.TeamWall;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 6:51
 */
@Data
public class TeamWallVO implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 团队 id
     */
    private Long teamId;

    /**
     * 墙内容
     */
    private String content;

    /**
     * 创始人信息
     */
    private UserDTO user;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param teamWallVO
     * @return
     */
    public static TeamWall voToObj(TeamWallVO teamWallVO) {
        if (teamWallVO == null) {
            return null;
        }
        TeamWall teamWall = new TeamWall();
        BeanUtils.copyProperties(teamWallVO, teamWall);
        return teamWall;
    }

    /**
     * 对象转包装类
     *
     * @param teamWall
     * @return
     */
    public static TeamWallVO objToVo(TeamWall teamWall) {
        if (teamWall == null) {
            return null;
        }
        TeamWallVO teamWallVO = new TeamWallVO();
        BeanUtils.copyProperties(teamWall, teamWallVO);

        return teamWallVO;
    }
}
