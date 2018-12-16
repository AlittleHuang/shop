package com.shengchuang.config.shiro;

import com.shengchuang.shop.domain.User;
import com.shengchuang.shop.service.UserService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

@CommonsLog
public class UserRealm extends AuthorizingRealm {

    public static final String SESSION_NAME_USER = "session_user";
    public static final String SESSION_NAME_ADMIN = "session_admin";
    final static String ALGORITHM_NAME = "MD5";//密码加密算法
    final static int HASH_ITERATIONS = 3;//密码加密次数

    @Autowired
    private UserService userService;

    public UserRealm() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(ALGORITHM_NAME);
        credentialsMatcher.setHashIterations(HASH_ITERATIONS);
        setCredentialsMatcher(credentialsMatcher);
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;

        String username = userToken.getUsername();

        User user = userService.findOneByAttrs("username", username);
        if (user == null) {
            throw new UnknownAccountException();
        }

        return new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getId()), getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User user = (User) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Session session = SecurityUtils.getSubject().getSession();
//        if (user.getRole() == 1 && null != session.getAttribute(SESSION_NAME_ADMIN)) {
//            info.addRole("admin");//角色
//        }
//        if (user.getRole() >= 0 && null != session.getAttribute(SESSION_NAME_USER)) {
//            info.addRole("front");
//        }
        return info;
    }

    public void clearCache() {
        PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
        clearCache(principalCollection);
    }
}
