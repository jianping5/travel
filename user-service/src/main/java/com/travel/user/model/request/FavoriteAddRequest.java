package com.travel.user.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "创建收藏夹请求体")
public class FavoriteAddRequest implements Serializable {

    /**
     * 用户 id
     */
    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(required = true,value = "收藏夹名称")
    /**
     * 收藏夹名称
     */
    private String favoriteName;

    private static final long serialVersionUID = 1L;

}
