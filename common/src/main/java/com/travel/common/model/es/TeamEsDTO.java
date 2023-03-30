package com.travel.common.model.es;

import com.travel.common.model.dto.TeamDTO;
import com.travel.common.model.vo.TeamVDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 28/3/2023 下午 5:36
 */
@Data
public class TeamEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 创建人 id
     */
    private Long userId;

    /**
     * 团队名
     */
    private String teamName;

    /**
     * 团队介绍
     */
    private String intro;

    /**
     * 创建时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

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
