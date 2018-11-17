package com.shengchuang.member.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;

import com.shengchuang.member.additional.domain.PwdProtected;
import com.shengchuang.member.additional.service.PwdProtectedService;
import com.shengchuang.member.additional.service.SmsImpl;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.sms.Sms;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.shiro.PasswordUtil;
import com.shengchuang.base.AbstractController;

@Controller
public class PwdProtectedController extends AbstractController {

    @Value("${profiles}")
    protected String profiles;
    @Autowired
    private PwdProtectedService pwdProtectedService;

    @PostMapping("/front/account/pwd/protected/update")
    public View update(PwdProtected pwdProtected) {
        pwdProtected.setUserId(getSessionUser().getId());
        pwdProtectedService.update(pwdProtected);
        return new JsonMap("修改成功");
    }

    @PostMapping("/account/pwd/protected/reset")
    public View resetPwd(PwdProtected pwdProtected, String username, String password) {
        Assert.notNull(pwdProtected.getType(), "请选择密保问题");
        Assert.notNull(pwdProtected.getAnswer(), "请填写密保答案");
        Assert.notNull(username, "未填写用户名");
        Assert.notNull(password, "未填写新密码");
        Assert.state(password.length() >= 6, "密码长度至少6位数");
        User user = userService.findByUsername(username);
        Assert.state(
                pwdProtectedService
                        .existsByUserIdAndTypeAndAnswer(user.getId(), pwdProtected.getType(), pwdProtected.getAnswer()),
                "密保问题或答案错误");
        password = PasswordUtil.encodeStringPassword(password, user.getUsername());
        user.setPassword(password);
        userService.save(user);
        return new JsonMap("修改成功");
    }
    /**
     * 手机号码找回密码
     * @param pwdProtected
     * @param username
     * @param password
     * @param repassword
     * @return
     */
    @PostMapping("/account/pwd/phoneReset")
    public View resetPhonePwd(PwdProtected pwdProtected, String username,String phone, String password, String secondpwd) {
    		User user =userService.findByUsernameAndPhone(username,"+(86)"+phone);
    		Assert.notNull(user, "该用户信息不正确");
    		Sms sms = null;
    		if(profiles.equals("prd")) {
    			sms = new SmsImpl(user.getPhone(),SmsImpl.SmsType.RESET_PASSWORD).verify(request().getParameter("phonecode"));
    		}
    		userService.resetLoginPassword(user.getId(), password, secondpwd);
    		if(sms!=null)sms.remove();
    		return new JsonMap("修改成功");
    }
}
