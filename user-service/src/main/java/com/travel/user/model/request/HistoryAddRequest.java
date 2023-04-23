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
@ApiModel(value = "添加浏览记录请求体")
public class HistoryAddRequest implements Serializable {

    @ApiModelProperty(hidden = true)
    /**
     * 用户 id
     */
    private Long userId;

    @ApiModelProperty(required = true,value = "浏览记录类型")
    /**
     * 浏览记录类型
     */
    private Integer historyObjType;

    @ApiModelProperty(required = true,value = "浏览对象 id")
    /**
     * 浏览对象 id
     */
    private Long historyObjId;


    private static final long serialVersionUID = 1L;

}
