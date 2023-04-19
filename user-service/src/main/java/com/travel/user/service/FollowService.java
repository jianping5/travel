package com.travel.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.user.model.dto.FollowVO;
import com.travel.user.model.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.user.model.request.FollowQueryRequest;
import com.travel.user.model.request.FollowRequest;

/**
* @author jianping5
* @description 针对表【follow(关注表)】的数据库操作Service
* @createDate 2023-03-22 14:34:09
*/
public interface FollowService extends IService<Follow> {

    Follow executeFollow(Follow follow);
    /**
     * 校验 Follow
     * @param follow
     * @param b
     */
    void validFollow(FollowRequest followRequest, boolean b);

    /**
     * 获取关注列表Page
     */
    Page<FollowVO> getFollowVOPage(Page<Follow> FollowPage);

    /**
     * 获取关注列表
     */
    Page<Follow> queryFollow(FollowQueryRequest followQueryRequest);

    

}
