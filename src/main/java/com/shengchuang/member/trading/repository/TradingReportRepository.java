package com.shengchuang.member.trading.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.trading.domain.TradingReport;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface TradingReportRepository
        extends CommonRepository<TradingReport, LocalDate>, JpaSpecificationExecutor<TradingReport> {
}
