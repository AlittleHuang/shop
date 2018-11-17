package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.StatisticalReport;
import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.base.AbstractService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class StatisticalReportService extends AbstractService<StatisticalReport, LocalDate> {


    public Page<StatisticalReport> getPage(PageRequestMap pageRequestMap) {
        Criteria<StatisticalReport> criteria = createCriteria(pageRequestMap).addOrderByDesc("date");
        addTimeFilter(criteria, pageRequestMap, "date");
        return getPage(criteria);
    }
}
