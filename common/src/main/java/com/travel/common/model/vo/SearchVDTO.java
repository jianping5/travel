package com.travel.common.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author jianping5
 * @createDate 28/3/2023 下午 4:57
 */
// todo: 待补充其他类型
@Data
public class SearchVDTO implements Serializable {


    private Page<TeamVDTO> teamVDTOPage;


    private Page<?> dataPage;
}
