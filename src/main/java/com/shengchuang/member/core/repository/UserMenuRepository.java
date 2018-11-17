package com.shengchuang.member.core.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.core.domain.UserMenu;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserMenuRepository extends CommonRepository<UserMenu, Integer>, JpaSpecificationExecutor<UserMenu> {

    UserMenu findByMenuIdAndUserId(Integer menuId, Integer userId);

    List<UserMenu> findByUserId(Integer userId);

}
