package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.StatisticalBalance;
import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticalBalanceService extends AbstractService<StatisticalBalance, Integer> {

    @Autowired
    UserService userService;

    public Page<StatisticalBalance> getPage(PageRequestMap pageRequestMap) {
        Criteria<StatisticalBalance> criteria = createCriteria(pageRequestMap);
        String username = pageRequestMap.get("username");
        if (username != null && username != "") {
            User user = userService.findByUsername(username);
            criteria.and().andEqual("userId", user.getId());
        }
        return getPage(criteria);
    }
}
