package com.travel.user.model.request;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:02
 */
@Data
public class MessageQueryRequest extends PageRequest implements Serializable {

    /**
     * 所属用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
