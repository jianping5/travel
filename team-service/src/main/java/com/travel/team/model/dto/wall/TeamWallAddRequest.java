package com.travel.team.model.dto.wall;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class TeamWallAddRequest implements Serializable {

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


    private static final long serialVersionUID = 1L;
}
