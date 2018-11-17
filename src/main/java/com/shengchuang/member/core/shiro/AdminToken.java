package com.shengchuang.member.core.shiro;

import com.shengchuang.common.util.ApplicationUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.repository.UserRepository;

public class AdminToken extends UserToken {

    private static final long serialVersionUID = -6107029008935097965L;

    public AdminToken(String username, String password) {
        super(username, password);
    }

    @Override
    protected User getDbUser(String username) {
        return ApplicationUtil.getBean(UserRepository.class).findByUsernameAndRole(username, 1);
    }
}
