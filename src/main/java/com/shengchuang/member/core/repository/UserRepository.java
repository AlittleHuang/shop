package com.shengchuang.member.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.core.domain.User;

public interface UserRepository
        extends CommonRepository<User, Integer>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

    User findByUsernameAndRole(String username, int role);

    List<User> findByReferrerId(Integer pid);

    List<User> findByLevel(int levle);

    boolean existsByIdAndUsernameAndPasswordAndSecondpwd(
            Integer id,
            String username,
            String password,
            String secondpwd);

    boolean existsByIdAndUsername(Integer id, String username);

    User findByPhone(String phone);

    List<User> findByPhoneAndIdNotIn(String phone, Integer[] userIds);

    long countByPhone(String phone);

	User findByUsernameAndPhone(String username, String phone);
}