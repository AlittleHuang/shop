package com.shengchuang.member.core.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.core.domain.PayInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface PayInfoRepository
        extends CommonRepository<PayInfo, Integer>, JpaSpecificationExecutor<PayInfo> {

    List<PayInfo> findByUserIdAndType(int userId, String type);

    List<PayInfo> findByType(String type);

    List<PayInfo> findByUserIdIn(Set<Integer> userIds);

    List<PayInfo> findByUserIdInAndTypeIn(Set<Integer> userIds, List<String> types);

    List<PayInfo> findByUserIdAndTypeAndStatusNot(Integer userId, String type, Integer delete);
}