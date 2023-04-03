package com.travel.team.model.vo;

import com.travel.common.model.dto.UserDTO;
import com.travel.team.model.entity.Team;
import com.travel.team.model.entity.TeamNews;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 6:51
 */
@Data
public class TeamNewsVO implements Serializable {

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
     * 动态内容
     */
    private String content;

    /**
     * 图片 URL
     */
    private String imageUrl;

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
     * @param teamNewsVO
     * @return
     */
    public static TeamNews voToObj(TeamNewsVO teamNewsVO) {
        if (teamNewsVO == null) {
            return null;
        }
        TeamNews teamNews = new TeamNews();
        BeanUtils.copyProperties(teamNewsVO, teamNews);
        return teamNews;
    }

    /**
     * 对象转包装类
     *
     * @param teamNews
     * @return
     */
    public static TeamNewsVO objToVo(TeamNews teamNews) {
        if (teamNews == null) {
            return null;
        }
        TeamNewsVO teamNewsVO = new TeamNewsVO();
        BeanUtils.copyProperties(teamNews, teamNewsVO);

        return teamNewsVO;
    }
}
