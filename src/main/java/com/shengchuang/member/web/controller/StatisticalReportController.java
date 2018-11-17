package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.domain.StatisticalReport;
import com.shengchuang.member.additional.service.StatisticalReportService;
import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

@RestController
public class StatisticalReportController extends AbstractController {

    @Autowired
    StatisticalReportService statisticalReportService;

    @RequestMapping("/admin/statistical/report")
    public View StatisticalReport() {
        Page<StatisticalReport> page = statisticalReportService.getPage(getPageRequestMap());
        Object sum = statisticalReportService.createCriteria()
                .addSelectSum("amount8", "amount9", "amount10", "amount11", "amount12").getOneObject();
        return new JsonMap(page).add("sum", sum);
    }

}
