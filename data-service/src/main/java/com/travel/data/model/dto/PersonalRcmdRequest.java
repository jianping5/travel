package com.travel.data.model.dto;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 个性化推荐请求体
 * @author jianping5
 * @createDate 11/4/2023 下午 8:21
 */
@Data
@ApiModel("个性化推荐请求体")
public class PersonalRcmdRequest extends PageRequest implements Serializable {

    /**
     * 推荐类型（团队、景区、文章游记、视频游记）
     */
    @ApiModelProperty(value = "推荐类型（团队、景区、文章游记、视频游记）")
    private Integer rcmdType;

    private static final long serialVersionUID = 1L;

}
