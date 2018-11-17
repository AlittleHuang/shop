package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.MiningMachine;
import com.shengchuang.common.mvc.repository.CommonRepository;

public interface MiningMachineRepository extends CommonRepository<MiningMachine, Integer> {
    MiningMachine findByUserId(Integer userId);
}
