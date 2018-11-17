package com.shengchuang.member.core.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.core.domain.Balance;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface BalanceRepository
        extends CommonRepository<Balance, Integer>, JpaSpecificationExecutor<Balance> {

    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    Balance findByUserIdAndType(int userId, int type);

    List<Balance> findByUserIdOrderByTypeAsc(int userId);

    List<Balance> findByTypeAndUserIdIn(int type, Collection<Integer> ids);

    List<Balance> findByType(int type);

    List<Balance> findByTypeAndAmountGreaterThan(int type, double amount);

    List<Balance> findByUserId(int userId);

    List<Balance> findByUserIdAndTypeBetween(int userId, int minType, int maxType);

    List<Balance> findByUserIdAndTypeIn(int userId, Collection<Integer> types);

    List<Balance> findByTypeIn(Collection<Integer> types);

    boolean existsByUserIdAndType(int userId, int type);

    List<Balance> findByTypeInAndUserIdIn(Collection<Integer> types, Collection<Integer> ids);

    List<Balance> findByUserIdInAndTypeIn(List<Integer> userIds, List<Integer> types);
}