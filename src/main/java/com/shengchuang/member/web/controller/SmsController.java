package com.shengchuang.member.web.controller;

import static com.shengchuang.member.web.controller.CaptchaController.VERIFICATION_CODE;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;

import com.shengchuang.member.additional.service.SmsImpl;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.base.AbstractController;

/**
 * 短信验证码
 */
@Controller
public class SmsController extends AbstractController {

	/**
	 * 修改银行资料验证码
	 */
	@PostMapping("/front/user/info/update/sms/send")
	public View sendUpdateSms() {
		// TODO 发短信
		return new JsonMap("发送成功");
	}

	/**
	 * 修改交易密码
	 */
	@PostMapping("/front/user/secondpwd/reset/sms/send")
	public View sendResetSecondpwdSms() {
		// TODO 发短信
		return new JsonMap("发送成功");
	}

	/**
	 * 注册
	 */
	@PostMapping("/front/msm/register/send")
	public View sendRegisterSms() {
		new SmsImpl(getPhone(), SmsImpl.SmsType.REGISTER).send();
		return new JsonMap("发送成功");
	}

	/**
	 * 注册
	 */
	@PostMapping("/front/msm/register/sendPc")
	public View sendRegisterSmsPc() {
		User user = refreshSessionUser();
		Assert.state(user.getPhone() != null && !"".equals(user.getPhone()), "请完善个人手机号码");
		new SmsImpl(user.getPhone(), SmsImpl.SmsType.REGISTER, user.getUsername()).send();
		return new JsonMap("发送成功");
	}

	/**
	 * 找回密码
	 */
	@PostMapping("/msm/password/reset/send")
	public View sendResetPwdSms(String phone) {
		boolean userExists = userService.createCriteria().andEqual("phone", phone).exists();
		Assert.state(userExists, "手机号码不存在");

		new SmsImpl(phone, SmsImpl.SmsType.RESET_PASSWORD).send();
		// TODO 发短信
		return new JsonMap("发送成功");
	}

	/**
	 * 找回密码加用户名控制
	 */
	@PostMapping("/msm/pwdAndScpwd/reset/send")
	public View sendResetPwdAndScPwdSms(String phone, String username) {
		HttpSession session = getSession();
		String sessionCode = (String) session.getAttribute(VERIFICATION_CODE);
		String vcode = request().getParameter("vCode");
		session.removeAttribute(VERIFICATION_CODE);
		Assert.state(vcode != null && vcode.equalsIgnoreCase(sessionCode), "图形验证码错误");
		boolean userExists = userService.createCriteria().andEqual("phone", "+(86)"+phone).andEqual("username", username)
				.exists();
		Assert.state(userExists, "用户信息与手机号码信息不符");
		new SmsImpl("+(86)"+phone, SmsImpl.SmsType.RESET_PASSWORD).send();
		// TODO 发短信
		return new JsonMap("发送成功");
	}

	/**
	 * 提现
	 */
	@PostMapping("/msm/withdraw/send")
	public View sendWithdrawSms() {
		User user = getSessionUser();
		Assert.state(user.getPhone() != null, "手机号码不存在");
		String phone = user.getPhone();
		new SmsImpl(phone, SmsImpl.SmsType.WITHDRAW).send();
		return new JsonMap("发送成功");
	}

	/**
	 * 转账
	 */
	@PostMapping("/msm/transfer/send")
	public View sendTransferSms() {
		String phone = getPhone();
		boolean userExists = userService.createCriteria().andEqual("phone", phone).exists();
		Assert.state(userExists, "手机号码不存在");
		new SmsImpl(phone, SmsImpl.SmsType.TRANSFER).send();
		return new JsonMap("发送成功");
	}

	/**
	 * 转账
	 */
	@PostMapping("/msm/transferPc/send")
	public View sendTransferSmsPc() {
		User user = refreshSessionUser();
		String phone = user.getPhone();
		Assert.state(phone != null && !"".equals(phone), "请完善个人手机号码");
		new SmsImpl(phone, SmsImpl.SmsType.TRANSFER, user.getUsername()).send();
		return new JsonMap("发送成功");
	}

	private void markQequestTime() {
		String servletPath = request().getServletPath();
		getSession().setAttribute(servletPath, System.currentTimeMillis());
	}
}
