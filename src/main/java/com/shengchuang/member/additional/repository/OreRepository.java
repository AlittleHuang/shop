package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.Ore;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OreRepository
        extends CommonRepository<Ore, Integer>, JpaSpecificationExecutor<Ore> {
}
