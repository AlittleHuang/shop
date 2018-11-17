package com.shengchuang.member.core.shiro;

import com.shengchuang.member.core.domain.User;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.ByteSource;

public abstract class UserToken extends UsernamePasswordToken {

    public static final User EMPTY = new User();
    private static final long serialVersionUID = 328172916269233346L;
    protected User user;

    public UserToken(String username, String password) {
        super(username, password);
    }

    protected abstract User getDbUser(String username);

    private User user() {
        if (user == null) {
            user = getDbUser(getUsername());
            if (user == null) {
                user = EMPTY;
            }
        }
        return user;
    }

    @Override
    public Object getPrincipal() {
        return user();
    }

    public String getDbPassword() {
        return user().getPassword();
    }

    public ByteSource getSalt() {
        return PasswordUtil.getSolt(user());
    }
}