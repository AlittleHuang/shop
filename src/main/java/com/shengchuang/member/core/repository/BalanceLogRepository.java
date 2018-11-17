package com.shengchuang.member.core.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.core.domain.BalanceLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BalanceLogRepository
        extends CommonRepository<BalanceLog, Integer>, JpaSpecificationExecutor<BalanceLog> {

}