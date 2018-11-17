package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.Contact;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContactRepository
        extends CommonRepository<Contact, Integer>, JpaSpecificationExecutor<Contact> {

    boolean existsByUserIdAndContactId(int userId, int contactID);

}
