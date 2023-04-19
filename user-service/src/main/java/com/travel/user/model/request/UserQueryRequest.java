package com.travel.user.model.request;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 所管官方 id
     */
    private Long officialId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 团队 id
     */
    private String teamId;

    /**
     * 代币数
     */
    private Integer token;

    /**
     * 发布地理位置
     */
    private String location;

    /**
     * 类型 id
     */
    private Integer typeId;

    /**
     * 用户角色
     */
    private Integer userRole;

    /**
     * 用户状态
     */
    private Integer userState;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
