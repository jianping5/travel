package com.travel.team.model.dto.wall;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
public class TeamWallQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 搜索词
     */
    private String searchText;

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
