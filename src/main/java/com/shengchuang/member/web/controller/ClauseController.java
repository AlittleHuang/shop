package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.domain.Clause;
import com.shengchuang.member.additional.service.ClauseService;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.List;

@RestController
public class ClauseController extends AbstractController {

    @Autowired
    private ClauseService informService;

    @RequestMapping("/admin/clause/save")
    public View save(Clause clause) {
        informService.update(clause);
        return new JsonMap("保存成功");
    }

    @RequestMapping("/admin/clause/get")
    public View get() {
        List<Clause> list = informService.findAll();
        return new JsonVO(list);
    }

    @RequestMapping("/clause/get")
    public View frontGet() {
        List<Clause> list = informService.findAll();
        return new JsonVO(list);
    }
}
