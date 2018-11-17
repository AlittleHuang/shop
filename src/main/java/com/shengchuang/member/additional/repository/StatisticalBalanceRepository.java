package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.StatisticalBalance;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatisticalBalanceRepository
        extends CommonRepository<StatisticalBalance, Integer>, JpaSpecificationExecutor<StatisticalBalance> {

}
