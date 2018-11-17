package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.Clause;
import com.shengchuang.base.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class ClauseService extends AbstractService<Clause, Integer> {

    public void update(Clause clause) {
        save(clause);
    }

}
