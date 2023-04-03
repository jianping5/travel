package com.travel.team.model.dto.team;

import com.travel.team.model.entity.Team;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 28/3/2023 下午 5:36
 */
// todo 取消注释开启 ES（须先配置 ES）
@Document(indexName = "team")
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
     * 团队状态（0：正常 1：异常 2：已解散）
     */
    private Integer teamState;

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


    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param teamEsDTO
     * @return
     */
    public static Team dtoToObj(TeamEsDTO teamEsDTO) {
        if (teamEsDTO == null) {
            return null;
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamEsDTO, team);
        return team;
    }

    /**
     * 对象转包装类
     *
     * @param team
     * @return
     */
    public static TeamEsDTO objToDto(Team team) {
        if (team == null) {
            return null;
        }
        TeamEsDTO teamEsDTO = new TeamEsDTO();
        BeanUtils.copyProperties(team, teamEsDTO);

        return teamEsDTO;
    }

}
