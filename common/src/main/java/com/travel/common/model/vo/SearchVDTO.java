package com.travel.common.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索结果视图体 page
 * @author jianping5
 * @createDate 28/3/2023 下午 4:57
 */
// todo: 待补充其他类型
@Data
public class SearchVDTO implements Serializable {

    /**
     * 团队视图体 page
     */
    private Page<TeamVDTO> teamVDTOPage;

    /**
     * 官方视图体 page
     */
    private Page<OfficialVDTO> officialVDTOPage;

    /**
     * 周边视图体 page
     */
    private Page<DerivativeVDTO> derivativeVDTOPage;

    /**
     * 用户视图体 page
     */
    private Page<UserVDTO> userVDTOPage;

    /**
     * 文章游记试视图体
     */
    private Page<ArticleVDTO> articleVDTOPage;

    /**
     * 视频游记视图体
     */
    private Page<VideoVDTO> videoVDTOPage;

    /**
     * 通用视图体 page
     */
    private Page<?> dataPage;

    private static final long serialVersionUID = 1L;
}
