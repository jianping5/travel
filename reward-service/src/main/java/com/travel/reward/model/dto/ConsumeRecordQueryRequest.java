package com.travel.reward.model.dto;

import com.travel.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 消费记录查询请求体
 * @author jianping5
 * @createDate 15/4/2023 下午 7:55
 */
@Data
@ApiModel(value = "消费记录查询请求体")
public class ConsumeRecordQueryRequest extends PageRequest implements Serializable {

    /**
     * 消费记录 id
     */
    @ApiModelProperty(value = "消费记录 id")
    private Long id;

    /**
     * 用户 id
     */
    @ApiModelProperty(value = "用户 id", required = true)
    private Long userId;

    /**
     * 消费信息
     */
    @ApiModelProperty(value = "消费信息", required = true)
    private String content;

    /**
     * 消费关联类型
     */
    @ApiModelProperty(value = "消费关联类型", required = true)
    private Integer consumeType;

    /**
     * 消费关联对象 id
     */
    @ApiModelProperty(value = "消费关联对象 id", required = true)
    private Long consumeId;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted;

    /**
     * 搜索词
     */
    @ApiModelProperty(value = "搜索词")
    private String searchText;

    private static final long serialVersionUID = 1L;
}
