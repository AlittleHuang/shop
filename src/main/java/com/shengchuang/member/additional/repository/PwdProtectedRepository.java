package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.PwdProtected;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PwdProtectedRepository
        extends CommonRepository<PwdProtected, Integer>, JpaSpecificationExecutor<PwdProtected> {
    void deleteByUserId(int userId);

    boolean existsByUserIdAndTypeAndAnswer(int userId, int type, String answer);
}