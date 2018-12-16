package com.shengchuang.shop.web.controller;

import com.shengchuang.base.AbstractController;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.shop.service.RegionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegionsController extends AbstractController {

    @Autowired
    private RegionsService regionsService;

    @RequestMapping(value = "/api/buyer/pca.json")
    public String getPcaData() {
        response().setHeader("Cache-Control", "max-age=" + TimeUtil.MILLIS_PER_DAY);
        return RegionsService.getData();
    }

}
