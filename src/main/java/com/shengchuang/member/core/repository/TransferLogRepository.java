package com.shengchuang.member.core.repository;

import com.shengchuang.common.mvc.repository.CommonRepository;
import com.shengchuang.member.core.domain.TransferLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransferLogRepository
        extends CommonRepository<TransferLog, Integer>, JpaSpecificationExecutor<TransferLog> {
}
