package com.travel.data.model.dto;

import com.travel.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 11/4/2023 下午 8:21
 */
@Data
public class PersonalRcmdRequest extends PageRequest implements Serializable {

    /**
     * 推荐类型（团队、景区、文章游记、视频游记）
     */
    private Integer rcmdType;

    private static final long serialVersionUID = 1L;

}
