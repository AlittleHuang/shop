package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.Clause;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClauseRepository extends CommonRepository<Clause, Integer>, JpaSpecificationExecutor<Clause> {

}
