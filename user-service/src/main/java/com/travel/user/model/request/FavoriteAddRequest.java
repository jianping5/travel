package com.travel.user.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class FavoriteAddRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 收藏夹名称
     */
    private String favoriteName;

    private static final long serialVersionUID = 1L;

}
