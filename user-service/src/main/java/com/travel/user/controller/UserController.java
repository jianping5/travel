package com.travel.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author jianping5
 * @createDate 18/3/2023 下午 9:40
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/")
    public String get() {
        return "123";
    }
}
