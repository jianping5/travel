package com.travel.user.model.request;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
@ApiModel(value = "获取消息列表请求体")
public class MessageQueryRequest extends PageRequest implements Serializable {

    @ApiModelProperty(required = true,value = "所属用户 id")
    /**
     * 所属用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
