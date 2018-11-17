package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InvestmentRepository
        extends JpaRepository<Investment, Integer>, JpaSpecificationExecutor<Investment> {

    List<Investment> findByUserId(Integer userId);

    List<Investment> findByUserIdAndType(Integer userId, Integer type);

    List<Investment> findByUserIdAndStatus(Integer userId, Integer status);

    List<Investment> findByIdAndType(Integer id, Integer type);

    List<Investment> findByStatus(int status);
}
