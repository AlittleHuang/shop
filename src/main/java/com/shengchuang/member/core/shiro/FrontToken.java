package com.shengchuang.member.core.shiro;

import com.shengchuang.common.util.ApplicationUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.repository.UserRepository;

public class FrontToken extends UserToken {
    private static final long serialVersionUID = 328172916269233346L;

    public FrontToken(String username, String password) {
        super(username, password);
    }

    @Override
    protected User getDbUser(String username) {
        User user = ApplicationUtil.getBean(UserRepository.class).findByUsername(username);
        if (user != null)
            return user;
        return ApplicationUtil.getBean(UserRepository.class).findByPhone(username);
    }

}