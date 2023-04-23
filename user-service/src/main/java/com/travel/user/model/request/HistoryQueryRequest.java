package com.travel.user.model.request;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zgy
 * @createDate 23/3/2023 下午 2:02
 */
@Data
@ApiModel(value = "查询浏览记录请求体")
public class HistoryQueryRequest extends PageRequest implements Serializable {

    @ApiModelProperty(required = true,value = "所属用户 id")
    /**
     * 所属用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
