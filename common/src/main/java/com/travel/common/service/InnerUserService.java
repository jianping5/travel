package com.travel.common.service;

import com.travel.common.model.dto.UserDTO;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 8:57
 */
public interface InnerUserService {
    String sayWorld();

    UserDTO getUser(Long id);
}
