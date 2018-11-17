package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.Inform;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InformRepository
        extends CommonRepository<Inform, Integer>, JpaSpecificationExecutor<Inform> {

    Inform findFistByIdGreaterThanOrderByIdDesc(int id);

    Inform findFistByIdLessThanOrderByIdAsc(int id);
}