package com.shengchuang;

import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.service.BalanceLogService;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTest {

    protected Map<Object, User> map = new HashMap<>();
    @Autowired
    UserService userService;
    @Autowired
    BalanceService balanceService;
    @Autowired
    BalanceLogService balanceLogService;

    protected User insertUser(Object username) {
        return insertUser(username, (Integer) null);
    }

    protected User insertUser(Object username, User pUser) {
        if (pUser != null)
            return insertUser(username, pUser.getId());
        return insertUser(username);
    }


    protected User insertUser(Object username, Integer pid) {
        User user = new User();
        user.setUsername(username.toString());
        user.setReferrerId(pid);
        User save = userService.save(user);
        map.put(username, user);
        return save;
    }


}
