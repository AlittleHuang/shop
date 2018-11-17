package com.shengchuang.member.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;

import com.shengchuang.member.additional.service.SmsImpl;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.sms.Sms;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.service.TransferService;
import com.shengchuang.base.AbstractController;

@Controller
public class TransferController extends AbstractController {

	@Autowired
	private TransferService transferService;

	@Value("${profiles}")
	private String profiles;

	/**
	 * 转账
	 */
	@PostMapping("/front/balance/transfer")
	public View transfer(String username, Integer balanceType, Double amount) {
		checkSecondPwd();
		User user = refreshSessionUser();
		Assert.state(!user.getUsername().equals(username), "不能给自己转账");
		Sms sms = null;
		if ("prd".equals(profiles)) {
			sms = new SmsImpl(user.getPhone(), SmsImpl.SmsType.TRANSFER).verify(request().getParameter("phonecode"));
		}
		User target = userService.findByUsername(username);
		Assert.notNull(target, "用户不存在");
		Assert.notNull(amount, "请输入数量");
		Assert.notNull(balanceType, "缺少参数:balanceType");
		List<BalanceType> displays = BalanceType.displays();
		Assert.state(displays.contains(BalanceType.of(balanceType)), "balanceType参数错误");
		transferService.transfer(user, target, balanceType, amount);
		if (sms != null)
			sms.remove();
		return new JsonMap("转账成功");
	}

	/**
	 * 积分兑换
	 */
	@PostMapping("/front/balance/converted")
	public View converted(Integer typeOut, Integer typeIn, Double amount) {
		checkSecondPwd();
		transferService.converted(getSessionUser(), typeOut, typeIn, amount);
		return new JsonMap("转出成功");
	}

	@GetMapping("/front/transfer/user/info/{userId}")
	public View userInfo(@PathVariable Integer userId) {
		User user = userService.getOne(userId);
		JsonMap jsonMap = new JsonMap();
		if (user != null) {
			jsonMap.add("username", user.getUsername());
			jsonMap.add("imgsrc", user.getImgsrc());
		} else {
			jsonMap.success(false);
		}
		return jsonMap;
	}

}
