package com.travel.team.model.vo;

import com.travel.common.model.dto.UserDTO;
import com.travel.team.model.entity.TeamApply;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 10/4/2023 下午 5:53
 */
@Data
public class TeamApplyVO implements Serializable {

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
     * 申请状态（0：审批中 1：同意 2：拒绝）
     */
    private Integer auditState;

    /**
     * 团队状态（0：正常 1：异常 2：已解散）
     */
    private Integer teamState;


    /**
     * 用户
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
     * @param teamApplyVO
     * @return
     */
    public static TeamApply voToObj(TeamApplyVO teamApplyVO) {
        if (teamApplyVO == null) {
            return null;
        }
        TeamApply teamApply = new TeamApply();
        BeanUtils.copyProperties(teamApplyVO, teamApply);
        return teamApply;
    }

    /**
     * 对象转包装类
     *
     * @param teamApply
     * @return
     */
    public static TeamApplyVO objToVo(TeamApply teamApply) {
        if (teamApply == null) {
            return null;
        }
        TeamApplyVO teamApplyVO = new TeamApplyVO();
        BeanUtils.copyProperties(teamApply, teamApplyVO);

        return teamApplyVO;
    }
}
