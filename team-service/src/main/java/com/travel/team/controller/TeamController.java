package com.travel.team.controller;

import com.travel.common.model.dto.UserDTO;
import com.travel.common.service.InnerUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jianping5
 * @createDate 18/3/2023 下午 9:48
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    @DubboReference
    private InnerUserService innerUserService;

    @GetMapping("/")
    public String get(HttpServletRequest request) {
        String world = innerUserService.sayWorld();
        System.out.println(request.getSession().getId());
        UserDTO userDTO = innerUserService.getUser(1L);
        System.out.println(userDTO.getUserName());
        return world;
    }

}
