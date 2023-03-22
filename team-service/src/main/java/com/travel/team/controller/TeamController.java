package com.travel.team.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jianping5
 * @createDate 18/3/2023 下午 9:48
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    @GetMapping("/")
    public String get() {
        return "567";
    }
}
