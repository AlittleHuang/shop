package com.shengchuang.member.web.controller;

import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.util.*;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.shiro.PasswordUtil;
import com.shengchuang.member.core.shiro.UserRealm;
import com.shengchuang.base.AbstractController;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
public class OauthController extends AbstractController {

    @Autowired
    private UserRealm userRealm;

    /**
     * 服务器调用,验证用户名密码
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/api/oauth/server/login")
    public View loginCheck(String username, String password) {
        Assert.hasText(username, "username 参数错误");
        Assert.hasText(password, "password 参数错误");
        String pwd = PasswordUtil.encodeStringPassword(password, username);
        boolean exists = userService.createCriteria().andEqual("username", username).andEqual("password", pwd).exists();
        String token = null;
        long now = System.currentTimeMillis();
        if (exists) {
            token = new Token(username, password).toString(String.valueOf(now));
        }
        return new JsonMap().success(exists).add("time", now).add("token", token);
    }

    /**
     * 服务器调用,验证 token, 通过 {@link OauthController#getToken(java.lang.String)}获取
     *
     * @param token
     * @param time
     * @return
     */
    @RequestMapping("/api/oauth/server/login/token")
    public View loginByToken(String token, Long time) {
        Assert.state(System.currentTimeMillis() - time < TimeUtil.MILLIS_PER_MINUTE * 5, "token 已过期");
        Assert.hasText(token, "token 参数错误");
        Assert.notNull(time, "time 参数错误");
        JsonMap jsonMap = new JsonMap();
        try {
            String decrypt = StringSecucituUtil.decrypt(token, time.toString());
            Token t = JsonUtil.decode(decrypt, Token.class);
            User user = userService.findByUsername(t.getUsername());
            Assert.notNull(user, "");
            Assert.state(user.getPassword().equals(t.getPassword()), "");
            jsonMap.add("user", user);
        } catch (Exception e) {
            jsonMap.success(false);
        }
        return jsonMap;
    }

    /**
     * 浏览器调用, 验证token
     *
     * @param token
     * @param time
     * @param url   跳转地址
     * @return
     */
    @RequestMapping("/api/oauth/login/token")
    public ModelAndView checkToken(String token, Long time, String url) {
        Assert.notNull(time, "time 参数错误");
        Assert.state(System.currentTimeMillis() - time < TimeUtil.MILLIS_PER_MINUTE * 5, "token 已过期");
        try {
            String decrypt = StringSecucituUtil.decrypt(token, String.valueOf(time));
            Token t = JsonUtil.decode(decrypt, Token.class);
            User login;
            if (hasRole("admin")) {
                login = userService.login(t.username, t.password);
            } else {
                login = UserController.login(t.username, t.password);
            }
            Assert.notNull(login, "账号或密码错误");
            Assert.state(login.getLevel() > 0, "无效账户");
            Assert.state((login.getFreeze() == null ? User.FREEZE_JD : login.getFreeze()) == User.FREEZE_JD,
                    "您的账号已被冻结，请联系管理员解冻！");
            setSessionUser(login);
            userRealm.clearCache();
        } catch (Exception e) {
            logger.debug("", e);
            return null;
        }
        return new ModelAndView("redirect:" + (StringUtil.isEmpty(url) ? "/" : url));
    }

    /**
     * 浏览器调用, 获取token
     *
     * @param url 跳转地址
     * @return
     */
    @RequestMapping("/api/oauth/login/check")
    public ModelAndView getToken(String url) {
        JsonMap jsonMap = new JsonMap();
        if (hasRole("front")) {
            Long now = System.currentTimeMillis();
            User user = getSessionUser();
            String token = new Token(user.getUsername(), user.getPassword()).toString(now.toString());
            jsonMap.add("time", now).add("token", token);
        } else {
            jsonMap.success(false);
        }
        return new ModelAndView("redirect:" + (StringUtil.isEmpty(url) ? "/" : url)).addAllObjects(jsonMap);
    }

    @Data
    @NoArgsConstructor
    private static class Token {
        String username;
        String password;

        public Token(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String toString(String salt) {
            String encode = JsonUtil.encode(this);
            return StringSecucituUtil.encrypt(encode, salt);
        }
    }

}
