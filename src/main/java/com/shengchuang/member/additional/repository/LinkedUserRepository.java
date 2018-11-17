package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.LinkedUser;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LinkedUserRepository
        extends CommonRepository<LinkedUser, Integer>, JpaSpecificationExecutor<LinkedUser> {

    LinkedUser findByUserIdAndLinkedId(Integer userId, Integer linkedId);

    void deleteByUserIdAndLinkedId(Integer linkedId, Integer userId);

}
