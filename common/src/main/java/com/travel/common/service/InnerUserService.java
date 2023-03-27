package com.travel.common.service;

import com.travel.common.model.dto.UserDTO;

import java.util.List;
import java.util.Set;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 8:57
 */
public interface InnerUserService {
    String sayWorld();

    /**
     * 根据用户 id 获取 用户
     * @param id
     * @return
     */
    UserDTO getUser(Long id);


    /**
     * 根据 用户 id 数组获取用户数组
     */
    List<UserDTO> listByIds(Set<Long> userIdList);

}
