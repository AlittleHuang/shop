package com.shengchuang.member.core.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.core.domain.Menu;

import java.util.List;

public interface MenuRepository extends CommonRepository<Menu, Integer> {

    List<Menu> findByPidIsNull();

    List<Menu> findByPid(Integer id);

    List<Menu> findByPidIsNullAndIdIn(List<Integer> menu);

    List<Menu> findByPidAndIdIn(Integer id, List<Integer> menuIds);

}
