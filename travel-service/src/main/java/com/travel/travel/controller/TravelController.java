package com.travel.travel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 3:10
 */
@RestController
@RequestMapping("/travel")
public class TravelController {

    @GetMapping("/")
    public String get() {
        return "567";
    }
}
