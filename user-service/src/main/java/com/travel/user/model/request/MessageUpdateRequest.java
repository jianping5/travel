package com.travel.user.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
@ApiModel(value = "更新消息阅读状态请求体")
public class MessageUpdateRequest implements Serializable {


    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 消息状态（0：未读 1：已读）
     */
    @ApiModelProperty(value = "消息状态（0：未读 1：已读）")
    private Integer messageState;

    @ApiModelProperty(hidden = true)
    /**
     * 是否删除（0：未删除 1：已删除）
     */
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}
