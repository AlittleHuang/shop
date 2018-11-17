package com.shengchuang.member.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MappingController {

    /**
     * 根目路径定向
     *
     * @return
     */
    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/index.html";
    }

}
