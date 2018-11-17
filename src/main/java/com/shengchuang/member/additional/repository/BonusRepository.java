package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.Bonus;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BonusRepository
        extends CommonRepository<Bonus, Integer>, JpaSpecificationExecutor<Bonus> {

}