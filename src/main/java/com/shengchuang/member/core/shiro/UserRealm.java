package com.shengchuang.member.core.shiro;

import com.shengchuang.member.core.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

public class UserRealm extends AuthorizingRealm {

    public static final String SESSION_NAME_USER = "session_user";
    public static final String SESSION_NAME_ADMIN = "session_admin";
    final static String ALGORITHM_NAME = "MD5";//密码加密算法
    final static int HASH_ITERATIONS = 3;//密码加密次数
    protected final Log logger = LogFactory.getLog(getClass());

    {
        initUserRealm();
    }

    private void initUserRealm() {
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
        UserToken userToken = (UserToken) token;
        Object principal = userToken.getPrincipal();
        if (principal == null || principal == UserToken.EMPTY)
            throw new AuthenticationException("User [" + userToken.getUsername() + "] does not exist");
        Object hashedCredentials = userToken.getDbPassword();
        ByteSource credentialsSalt = userToken.getSalt();

        String realmName = getName();
        return new SimpleAuthenticationInfo(principal, hashedCredentials, credentialsSalt, realmName);
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User user = (User) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Session session = SecurityUtils.getSubject().getSession();
        if (user.getRole() == 1 && null != session.getAttribute(SESSION_NAME_ADMIN)) {
            info.addRole("admin");//角色
        }
        if (user.getRole() >= 0 && null != session.getAttribute(SESSION_NAME_USER)) {
            info.addRole("front");
        }
        return info;
    }

    public void clearCache() {
        PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
        clearCache(principalCollection);
    }
}
