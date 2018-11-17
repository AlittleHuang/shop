package com.shengchuang.member.trading.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.trading.domain.TradeOrder;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TradeOrderRepository
        extends CommonRepository<TradeOrder, Integer>, JpaSpecificationExecutor<TradeOrder> {


}
