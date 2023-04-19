package com.travel.common.service;

import com.baomidou.mybatisplus.extension.service.IService;

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

}
