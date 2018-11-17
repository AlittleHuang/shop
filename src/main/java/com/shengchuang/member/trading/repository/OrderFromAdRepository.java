package com.shengchuang.member.trading.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.trading.domain.OrderFromAd;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderFromAdRepository
        extends CommonRepository<OrderFromAd, Integer>, JpaSpecificationExecutor<OrderFromAd> {
}
