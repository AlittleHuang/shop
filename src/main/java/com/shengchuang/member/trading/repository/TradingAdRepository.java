package com.shengchuang.member.trading.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.trading.domain.TradingAd;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TradingAdRepository
        extends CommonRepository<TradingAd, Integer>, JpaSpecificationExecutor<TradingAd> {


}
