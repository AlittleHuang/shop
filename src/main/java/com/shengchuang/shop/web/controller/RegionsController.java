package com.shengchuang.shop.web.controller;

import com.shengchuang.shop.service.RegionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegionsController {

    @Autowired
    private RegionsService regionsService;

    @RequestMapping(value = "/buyer/pca")
    public String getPcaData() {
        return regionsService.getData();
    }

}
