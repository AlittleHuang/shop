package com.shengchuang.member.core.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.core.domain.Recharge;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RechargeRepository
        extends CommonRepository<Recharge, Integer>, JpaSpecificationExecutor<Recharge> {

}