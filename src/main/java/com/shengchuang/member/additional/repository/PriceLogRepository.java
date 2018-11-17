package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.PriceLog;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PriceLogRepository
        extends CommonRepository<PriceLog, Integer>, JpaSpecificationExecutor<PriceLog> {


}