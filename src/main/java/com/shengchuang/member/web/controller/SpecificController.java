package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.service.UserLevelService;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 本项目专有
 */
@RestController
public class SpecificController extends AbstractController {

    @Autowired
    private
    UserLevelService userLevelService;

//    @GetMapping("/front/user/level")
//    public View uscLevel() {
//        User user = getSessionUser();
//        userLevelService.loadUscLevel(user);
//        userLevelService.loadTeamLevel(user);
//        return new JsonMap().add("uscLevel", user.getUscLevel()).add("teamLevel", user.getTeamLevel());
//    }

}
