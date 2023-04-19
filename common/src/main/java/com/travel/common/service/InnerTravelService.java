package com.travel.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.model.dto.travel.*;
import com.travel.common.model.vo.ArticleVDTO;
import com.travel.common.model.vo.SearchVDTO;
import com.travel.common.model.vo.VideoVDTO;

import java.util.List;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 8:58
 */
public interface InnerTravelService {

    /**
     * 根据团队 id 和用户 id 更新对应游记的团队 id 为 0 （团队回收站 id）
     * @param userId
     * @param teamId
     * @return
     */
    boolean updateTravelByTeamId(Long userId, Long teamId);


    /**
     * 从 ES 中搜索分页的数据
     * @param articleQueryRequest
     * @return
     */
    Page<ArticleVDTO> searchFromEs(ArticleQueryRequest articleQueryRequest);

    /**
     * 从 ES 中搜索分页的数据
     * @param videoQueryRequest
     * @return
     */
    Page<VideoVDTO> searchFromEs(VideoQueryRequest videoQueryRequest);

    /**
     * 查询景区或游记的相关游记
     * @param travelRcmdRequest
     * @return
     */
    SearchVDTO listTravelRcmd(TravelRcmdRequest travelRcmdRequest);

    /**
     * 查询文章（用于奖励）
     * @return
     */
    List<ArticleDTO> listArticleForReward();

    /**
     * 查询视频（用于奖励）
     * @return
     */
    List<VideoDTO> listVideoForReward();


}
