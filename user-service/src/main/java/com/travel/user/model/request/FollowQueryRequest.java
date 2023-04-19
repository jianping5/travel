package com.travel.user.model.request;

import com.travel.common.common.PageRequest;
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
public class FollowQueryRequest extends PageRequest implements Serializable {

    /**
     * 所属用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
