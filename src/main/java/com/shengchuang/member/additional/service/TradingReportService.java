package com.shengchuang.member.additional.service;

import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.base.AbstractService;
import com.shengchuang.member.trading.domain.TradingReport;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TradingReportService extends AbstractService<TradingReport, LocalDate> {
    public Page<TradingReport> getPage(PageRequestMap pageRequestMap) {
        return createCriteria(pageRequestMap).addOrderByDesc("date").getFixPage();
    }
}
