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
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("获取收藏列表请求体")
public class CollectionQueryRequest extends PageRequest implements Serializable {

    @ApiModelProperty(value = "所属用户 id",required = true)
    /**
     * 所属用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
