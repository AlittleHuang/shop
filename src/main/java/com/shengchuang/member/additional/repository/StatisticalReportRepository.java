package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.StatisticalReport;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface StatisticalReportRepository
        extends CommonRepository<StatisticalReport, LocalDate>, JpaSpecificationExecutor<StatisticalReport> {
}
