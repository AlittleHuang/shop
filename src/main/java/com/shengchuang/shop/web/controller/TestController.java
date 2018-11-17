package com.shengchuang.shop.web.controller;

import com.shengchuang.base.AbstractController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("shopTestController")
public class TestController extends AbstractController {

    @RequestMapping("/user/test")
    public Object test() {
        return "hello";
    }

}
