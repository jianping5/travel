package com.travel.common.model.vo;

import com.travel.common.model.dto.TeamDTO;
import com.travel.common.model.dto.UserDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 2:36
 */
@Data
public class TeamVDTO implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建人 id
     */
    private Long userId;

    /**
     * 团队名
     */
    private String name;

    /**
     * 团队图标 URL
     */
    private String iconUrl;

    /**
     * 团队封面 URL
     */
    private String coverUrl;

    /**
     * 团队介绍
     */
    private String intro;

    /**
     * 团队人数
     */
    private Integer teamSize;

    /**
     * 团队容量
     */
    private Integer capacity;

    /**
     * 团队动态数
     */
    private Integer newsCount;

    /**
     * 团队游记数
     */
    private Integer travelCount;

    /**
     * 是否需要审核（0：不需要 1：需要）
     */
    private Integer isAudit;

    /**
     * 团队状态（0：正常 1：异常 2：已解散）
     */
    private Integer teamState;

    /**
     * 创始人信息
     */
    private UserDTO user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param teamVO
     * @return
     */
    public static TeamDTO voToObj(TeamVDTO teamVO) {
        if (teamVO == null) {
            return null;
        }
        TeamDTO team = new TeamDTO();
        BeanUtils.copyProperties(teamVO, team);
        return team;
    }

    /**
     * 对象转包装类
     *
     * @param team
     * @return
     */
    public static TeamVDTO objToVo(TeamDTO team) {
        if (team == null) {
            return null;
        }
        TeamVDTO teamVO = new TeamVDTO();
        BeanUtils.copyProperties(team, teamVO);

        return teamVO;
    }
}
