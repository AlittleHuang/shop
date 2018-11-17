package com.shengchuang.member.trading.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.trading.domain.Trading;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;

public interface TradingRepository
        extends CommonRepository<Trading, Integer>, JpaSpecificationExecutor<Trading> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Trading findByUserIdAndType(Integer userId, Integer type);

    @Query(value = "SELECT SUM(buy_amount) totalAmount FROM tranding WHERE createtime &lt;#{starttime}", nativeQuery = true)
    long getTotalCount();

}
