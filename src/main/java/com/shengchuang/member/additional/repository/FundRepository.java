package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.Fund;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FundRepository
        extends CommonRepository<Fund, Integer>, JpaSpecificationExecutor<Fund> {

}