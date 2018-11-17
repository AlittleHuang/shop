package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.CustomerMessage;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerMessageRepository
        extends CommonRepository<CustomerMessage, Integer>, JpaSpecificationExecutor<CustomerMessage> {

}
