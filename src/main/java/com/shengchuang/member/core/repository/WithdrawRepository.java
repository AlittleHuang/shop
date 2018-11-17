package com.shengchuang.member.core.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.core.domain.Withdraw;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WithdrawRepository
        extends CommonRepository<Withdraw, Integer>, JpaSpecificationExecutor<Withdraw> {

}