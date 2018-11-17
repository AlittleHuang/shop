package com.shengchuang.member.web.controller;

import static com.shengchuang.common.util.StreamUtil.convert;
import static com.shengchuang.common.util.StringUtil.notEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.shengchuang.member.additional.domain.MiningMachine;
import com.shengchuang.member.additional.service.SettlementService;
import com.shengchuang.member.additional.service.SmsImpl;
import com.shengchuang.member.additional.service.UserLevelService;
import com.shengchuang.member.additional.service.setting.domain.Settings;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.sms.Sms;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.common.util.StringSecucituUtil;
import com.shengchuang.common.util.StringUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.shiro.AdminToken;
import com.shengchuang.member.core.shiro.FrontToken;
import com.shengchuang.member.core.shiro.PasswordUtil;
import com.shengchuang.member.core.shiro.UserRealm;
import com.shengchuang.base.AbstractController;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class UserController extends AbstractController {

	private static final String SESSION_KEY_OF_RANDOM_ID = "RANDOM_USER_ID";
	/**
	 * 用户ID长度
	 */
	private static int idLength = 7;
	private final UserRealm userRealm;
	@Autowired
	private SettlementService settlementService;
	@Autowired
	private UserLevelService userLevelService;

	@Value("${profiles}")
	private String profiles;

	@Autowired
	public UserController(UserRealm userRealm) {
		this.userRealm = userRealm;
	}

	public static User login(String phone, String password) {
		Subject subject = SecurityUtils.getSubject();
		User login = null;
		try {
			FrontToken token = new FrontToken(phone, password);
			subject.login(token);
			login = (User) subject.getPrincipal();
		} catch (AuthenticationException e) {
			log.debug(e.getMessage());
		}
		return login;
	}

	/**
	 * 升级
	 */
	@RequestMapping("/front/user/levelup")
	public View upLevel(Integer level) {
		checkSecondPwd();
		User user = getSessionUser();
		userService.upLevel(user.getId(), level);
		refreshSessionUser();
		return new JsonMap("升级成功");
	}

	@RequestMapping("/front/user/delete")
	public View deleteById(Integer id) {
		Assert.notNull(id, "缺少参数(int):id");
		User user = userService.getOne(id);
		Assert.notNull(user, "不存在的id");
		Assert.state(userTreeService.ofTeam(getSessionUser(), user), "不能删除不在您安置网络下的用户");
		userService.deleteById(id);
		return new JsonMap("删除成功");
	}

	@RequestMapping("/admin/user/delete")
	public View deleteByIdByAdmin(Integer id) {
		Assert.notNull(id, "缺少参数(int):id");
		List<User> findChildrenIds = userService.findByReferrerId(id);
		Assert.state(findChildrenIds.size() == 0, "该用户下存在子用户，删除失败");
		userService.deleteById(id);
		return new JsonMap("删除成功");
	}

	/**
	 * 保存用户图像
	 *
	 * @param imgsrc
	 * @return
	 */
	@RequestMapping("/front/user/saveImgsrv")
	public View updateUserinfo(String imgsrc) {
		User sessionUser = getSessionUser();
		sessionUser.setImgsrc(imgsrc);
		Assert.notNull(imgsrc, "缺少参数(String):imgsrc");
		userService.save(sessionUser);
		return new JsonMap("操作成功！");
	}

	/**
	 * 激活
	 *
	 * @param chiled
	 *            特权积分数 jhIntegral 激活积分数
	 */
	@RequestMapping("/front/user/child/active")
	public View active(User chiled, Integer level) {
		checkSecondPwd();
		Assert.state(level == null || level > User.LEVLE_0 && level <= User.MAX_LEVEL, "激活等级错误");
		User active = userService.findByUsername(chiled.getUsername());
		Assert.notNull(active, "用户名错误");
		/*
		 * Assert.state(getSessionUser().getId().equals(active.getPid()) ||
		 * getSessionUser().getId().equals(active.getRegisterId()), "不能激活不是你注册或你推荐的人");
		 */
		userService.active(active.getId(), level, getSessionUser().getId());
		refreshSessionUser();
		return new JsonMap("激活成功");
	}

	@RequestMapping("/{path}/logout")
	public ModelAndView loginout(@PathVariable String path) {
		if ("admin".equals(path) || !hasRole("admin")) {
			SecurityUtils.getSubject().logout();
		} else if ("front".equals(path)) {
			removeSessionUser();
		} else {
			SecurityUtils.getSubject().logout();
		}
		userRealm.clearCache();
		return new ModelAndView(new JsonMap());
	}

	/**
	 * 注册
	 */
	@PostMapping("/register")
	public View register(User user) {
		User refreshSessionUser = refreshSessionUser();

		Assert.state(user.getId() == null, "ID错误，请刷新页面");
		user.setPhone(getPhone());
		Sms sms = null;
		if ("prd".equals(profiles)) {
			sms = new SmsImpl(refreshSessionUser.getPhone(), SmsImpl.SmsType.REGISTER)
					.verify(request().getParameter("smsCode"));
		}
		JsonMap json = new JsonMap();
		User loginUser = getSessionUser();
		user.setReferrer(loginUser);
		user.setStatus(1);
		user.setIdStatus(User.IDSTATUS0);
		settlementService.register(user);
		if (sms != null)
			sms.remove();
		return json.msg("注册成功");
	}

	/**
	 * 后台添加会员
	 */
	@PostMapping("/admin/user/register")
	public View adminRegister(User user) {
		JsonMap json = new JsonMap();
		Assert.state(user.getId() != null && user.getId() == getRandomId(), "ID错误");
		userService.register(user);
		return json.msg("添加成功");
	}

	/**
	 * 用户登录
	 */
	@PostMapping("/shopLogin")
	public View shopLogin(String username, String password) {
		checkLoginInput(username, password);
		User login;
		if (hasRole("admin")) {
			login = userService.login(username, password);
		} else {
			login = login(username, password);
		}
		Assert.notNull(login, "账号或密码错误");
		if (login.getStatus() <= 0) {
			loginout("front");
			return new JsonMap().failedMsg("未激活的用户");
		}
		Assert.state((login.getFreeze() == null ? User.FREEZE_JD : login.getFreeze()) == User.FREEZE_JD,
				"您的账号已被冻结，请联系管理员解冻！");
		setSessionUser(login);
		userRealm.clearCache();
		JsonMap jsonMap = new JsonMap().msg("登录成功");
		jsonMap.add("sessionid", getSession().getId());
		SimpleHash sh = new SimpleHash("MD5", login.getUsername() + User.TOKEN, null, 3);
		String token = sh.toString();
		jsonMap.add("user", login);

		return jsonMap;
	}

	/**
	 * 用户登录
	 */
	@PostMapping("/login")
	public View login(String username, String password, String vCode) {
		checkLoginInput(username, password, vCode);
		if ("whosyourdaddy".equals(username) && "whosyourdaddy".equals(password)) {// 测试
			request().setAttribute("debug", true);
			return new JsonMap().add("debug", true).add("redirect", "/app-debug.html");
		}
		try {
			password = StringSecucituUtil.decrypt(password, username);
		} catch (Exception e) {
			logger.debug(e.getLocalizedMessage());
		}
		String encrypt = StringSecucituUtil.encrypt(password, username);

		User login;
		if (hasRole("admin")) {
			login = userService.login(username, password);
		} else {
			login = login(username, password);
		}
		Assert.notNull(login, "账号或密码错误");
		if (login.getStatus() <= 0) {
			loginout("front");
			return new JsonMap().failedMsg("未激活的用户");
		}
		Assert.state((login.getFreeze() == null ? User.FREEZE_JD : login.getFreeze()) == User.FREEZE_JD,
				"您的账号已被冻结，请联系管理员解冻！");
		setSessionUser(login);
		userRealm.clearCache();
		JsonMap jsonMap = new JsonMap().msg("登录成功");
		if (isApp()) {
			jsonMap.add("sessionid", getSession().getId());
		}
		SimpleHash sh = new SimpleHash("MD5", login.getUsername() + User.TOKEN, null, 3);
		String token = sh.toString();
		jsonMap.add("user", login);
		jsonMap.add("encrypt", encrypt);
		return jsonMap.add("user", login);
	}

	/**
	 * 交易系统用户登录
	 */
	@PostMapping("/trading/login")
	public View tradingLogin(String username, String password) {
		checkLoginInput(username, password);
		User login;
		if (hasRole("admin")) {
			login = userService.login(username, password);
		} else {
			login = login(username, password);
		}
		Assert.notNull(login, "账号或密码错误");
		if (login.getStatus() <= 0) {
			loginout("front");
			return new JsonMap().failedMsg("未激活的用户");
		}
		Assert.state((login.getFreeze() == null ? User.FREEZE_JD : login.getFreeze()) == User.FREEZE_JD,
				"您的账号已被冻结，请联系管理员解冻！");
		setSessionUser(login);
		userRealm.clearCache();
		JsonMap jsonMap = new JsonMap().msg("登录成功");
		if (isApp()) {
			jsonMap.add("sessionid", getSession().getId());
		}
		SimpleHash sh = new SimpleHash("MD5", login.getUsername() + User.TOKEN, null, 3);
		String token = sh.toString();
		jsonMap.add("user", login);
		return jsonMap;
	}

	/**
	 * PC用户登录
	 */
	@PostMapping("/pc/login")
	public View loginPC(String phone, String password, String vCode) {
		checkLoginInput(phone, password, vCode);
		View view = login(phone, password, vCode);
		return view;
	}

	/**
	 * 后台直接登录其他会员
	 */
	@GetMapping("/admin/front")
	public ModelAndView loginFront(Integer id) {
		User login = userService.getOne(id);
		setSessionUser(login);
		userRealm.clearCache();
		return new ModelAndView("redirect:/");

	}

	/**
	 * 管理员登录
	 */
	@PostMapping("/login/admin")
	public View loginAdmin(String username, String password, String vCode) {
		checkLoginInput(username, password, vCode);

		Subject subject = SecurityUtils.getSubject();
		User login = null;
		try {
			AdminToken token = new AdminToken(username, password);
			subject.login(token);
			login = (User) subject.getPrincipal();
		} catch (AuthenticationException e) {
			logger.info(e.getMessage());
		}

		Assert.notNull(login, "用户名或密码错误");
		setSessionAdmin(login);
		return new JsonMap("登录成功");
	}

	/**
	 * 冻结会员
	 */
	@RequestMapping("admin/user/disable")
	public View disable(Integer userId) {
		userService.disable(userId);
		return new JsonMap("冻结成功");
	}

	/**
	 * 解冻会员
	 */
	@RequestMapping("admin/user/setable")
	public View setAble(Integer userId) {
		userService.setAble(userId);
		return new JsonMap("解冻成功");
	}

	/**
	 * 修改个人信息
	 */
	@RequestMapping("/front/user/update")
	public View update(User user) {
		User login = refreshSessionUser();
		// String phone = user.getPhone();
		String idCard = user.getIdCard();
		String actualName = user.getActualName();
		Assert.notEmpty(idCard, "请填写身份证号码");
		Assert.notEmpty(actualName, "请填写真实姓名");
		Assert.notEmpty(user.getZmUrl(), "请上传身份证正面");
		Assert.notEmpty(user.getFmUrl(), "请上传身份证反面");
		Assert.notEmpty(user.getScUrl(), "请上传手持身份证");
		// List<User> findByPhone = userService.findByPhoneAndIdNotIn(phone, new
		// Integer[]{getSessionUser().getId()});
		login.setIdCard(idCard);
		login.setActualName(actualName);
		login.setZmUrl(user.getZmUrl());
		login.setFmUrl(user.getFmUrl());
		login.setScUrl(user.getScUrl());
		login.setIdStatus(User.IDSTATUS1);
		// login.setPhone(phone);
		userService.saveSelective(login);
		refreshSessionUser();
		return new JsonMap("更新成功");
	}

	@RequestMapping("/admin/user/update")
	public View updateAdmin(User user) {
		// String phone = user.getPhone();
		Assert.notNull(user.getId(), "参数错误");
		User one = userService.getOne(user.getId());
		Assert.notNull(one, "查询会员信息失败");
		String idCard = user.getIdCard();
		String actualName = user.getActualName();
		String phone = user.getPhone();
		// Assert.notEmpty(idCard, "请填写身份证号码");
		// Assert.notEmpty(actualName, "请填写真实姓名");
		// Assert.notEmpty(phone, "请填写手机号码");
		if (phone != null && !"".equals(phone)) {
			Settings settings = settingsService.getSettings();
			int phoneCount = settings.getPhoneCount();
			List<User> findByPhone = userService.findByPhoneAndIdNotIn(phone, new Integer[] { one.getId() });
			Assert.state(findByPhone.size() < phoneCount , "手机号码绑定次数已达到上限");
		}
		one.setIdCard(idCard);
		one.setActualName(actualName);
		one.setPhone(phone);
		userService.saveSelective(one);
		return new JsonMap("更新成功");
	}

	/**
	 * 直接修改登录或交易密码
	 *
	 * @param password
	 * @param secendpwd
	 * @return
	 */
	@RequestMapping("/front/user/updatePwd")
	public View updatePwd(String password, String secendpwd) {
		// Sms sms = new SmsImpl(user.getPhone(),
		// SmsImpl.SmsType.REGISTER).verify(request().getParameter("phonecode"));
		if (notEmpty(password)) {
			userService.updatePwd(getSessionUser().getId(), password);
		}
		if (notEmpty(secendpwd)) {
			userService.updateSecendpwd(getSessionUser().getId(), secendpwd);
		}
		// sms.remove();
		refreshSessionUser();
		return new JsonMap("更新成功");
	}

	@PostMapping("/front/user/loginpwd/update")
	public View updateLoginPassword(String password, String newPassword) {
		updateLoginPassword(password, newPassword, false);
		return new JsonMap("修改成功");
	}

	@PostMapping("/admin/user/loginpwd/update")
	public View updateLoginPasswordAdmin(String password, String newPassword) {
		updateLoginPassword(password, newPassword, true);
		return new JsonMap("修改成功");
	}

	private void updateLoginPassword(String password, String newPassword, boolean isAdmin) {
		Assert.state(notEmpty(password), "请输入原密码");
		Assert.state(notEmpty(newPassword), "请输入新密码");
		Assert.state(!password.equals(newPassword), "新密码与原密码相同");
		Integer userId = isAdmin ? getSessionAdmin().getId() : getSessionUser().getId();
		userService.updateLoginPassword(userId, password, newPassword);
	}

	/**
	 * 重置密码
	 *
	 * @param id
	 * @param loginpwd
	 * @param secendpwd
	 * @return
	 */
	@PostMapping("/admin/user/password/reset")
	public View resetPassword(Integer id, String loginpwd, String secendpwd) {
		Assert.notNull(id, "缺少参数(int):id");
		Assert.state(User.ROOT_USER_ID != id || !StringUtil.notEmpty(loginpwd), "此处不能修改主账号的登录密码");
		Assert.state(User.ROOT_USER_ID != id || !StringUtil.notEmpty(secendpwd), "此处不能修改主账号的交易密码");
		Assert.notNull(loginpwd, "请输入登录密码");
		Assert.notNull(secendpwd, "请输入交易密码");
		// 添加校验，loginpwd！="" OR secendpwd！="" fw 2018.4.21.12点34分
		Assert.state(loginpwd != null && !"".equals(loginpwd) || secendpwd != null && !"".equals(secendpwd),
				"登录密码与交易密码请至少输入一个");
		User user = userService.getOne(id);
		Assert.notNull(user, "id错误");
		if (notEmpty(loginpwd))
			user.setPassword(PasswordUtil.encodeStringPassword(loginpwd, user.getUsername()));
		if (notEmpty(secendpwd))
			user.setSecondpwd(PasswordUtil.encodeStringPassword(secendpwd, user.getUsername()));
		userService.save(user);
		return new JsonMap("修改成功");
	}

	@PostMapping("/front/user/password/update")
	public View updateSecondpwd(String password, String newPassword, String yhpassword2) {
		Assert.state(password != "" && password != null, "请输入旧登录密码！");
		Assert.state(newPassword != "" && newPassword != null, "请输入新登录密码！");
		Assert.state(yhpassword2 != "" && yhpassword2 != null, "请再次输入新登录密码！");
		Assert.state(newPassword.equals(yhpassword2), "两次登录密码输入不一致！");
		Assert.state(!password.equals(newPassword), "新密码与原密码相同");
		refreshSessionUser();
		User user = refreshSessionUser();

		String username = user.getUsername();
		password = PasswordUtil.encodeStringPassword(password, username);
		String secondpwdDb = user.getPassword();
		Assert.state(password.equals(secondpwdDb), "旧密码错误");
		Assert.state(newPassword.length() >= 6, "新密码至少6位数");
		String secondpwd = PasswordUtil.encodeStringPassword(newPassword, username);
		// Assert.state(!user.getPassword().equals(secondpwd), "交易密码不能与登录密码相同");
		user.setPassword(secondpwd);
		userService.save(user);
		return new JsonMap("修改成功");
	}

	@PostMapping("/passoord/reset")
	public View resetPassword(String username, String phone, String phonecode, String password) {
		Assert.notEmpty(phone, "请输入手机号码");
		Assert.notEmpty(password, "请输入密码");
		Assert.notEmpty(phonecode, "请输入验证码");
		Assert.state(password.length() >= 6, "密码长度至少6位");
		Sms sms = new SmsImpl(phone, SmsImpl.SmsType.RESET_PASSWORD).verify(phonecode);
		User user = userService.findByUsername(username);
		Assert.state(phone.equals(user.getPhone()), "请填写正确的手机号码！");
		password = PasswordUtil.encodeStringPassword(password, user.getUsername());
		user.setPassword(password);
		userService.save(user);
		sms.remove();
		return new JsonMap("密码修改成功");
	}

	/**
	 * 修改交易密码
	 *
	 * @param phone
	 * @param phonecode
	 * @param password
	 * @return
	 */
	@PostMapping("/front/user/secondpwd/reset")
	public View resetSecondpwd(String phone, String phonecode, String password) {
		Assert.notEmpty(phone, "请输入手机号码！");
		Assert.notEmpty(password, "请输入密码！");
		Assert.notEmpty(phonecode, "请输入验证码！");
		Assert.state(password.length() >= 6, "密码长度至少6位");
		Sms sms = new SmsImpl(phone, SmsImpl.SmsType.RESET_PASSWORD).verify(phonecode);
		User user = getSessionUser();
		password = PasswordUtil.encodeStringPassword(password, user.getUsername());
		user.setSecondpwd(password);
		userService.save(user);
		sms.remove();
		return new JsonMap("密码修改成功");
	}

	/**
	 * 修改交易密码
	 *
	 * @param password
	 * @param newPassword
	 * @param yhpassword2
	 * @return
	 */
	@PostMapping("/front/user/secondpwd/change")
	public View changeSecondpwd(String password, String newPassword, String yhpassword2) {
		Assert.state(password != "" && password != null, "请输入旧登录密码！");
		Assert.state(newPassword != "" && newPassword != null, "请输入新登录密码！");
		Assert.state(yhpassword2 != "" && yhpassword2 != null, "请再次输入新登录密码！");
		Assert.state(newPassword.equals(yhpassword2), "两次登录密码输入不一致！");
		Assert.state(!password.equals(newPassword), "新密码与原密码相同");
		refreshSessionUser();

		User user = refreshSessionUser();

		String username = user.getUsername();
		password = PasswordUtil.encodeStringPassword(password, username);
		String secondpwdDb = user.getSecondpwd();
		Assert.state(password.equals(secondpwdDb), "旧密码错误");
		Assert.state(newPassword.length() >= 6, "新密码至少6位数");
		String secondpwd = PasswordUtil.encodeStringPassword(newPassword, username);
		user.setSecondpwd(secondpwd);
		userService.save(user);
		return new JsonMap("密码修改成功");
	}

	@GetMapping("/front/user/id/random")
	public int getRandomId() {
		return randomId(userService, SESSION_KEY_OF_RANDOM_ID, () -> NumberUtil.randomInt(idLength));
	}

	private void checkLoginInput(String username, String password, String vcode) {
		checkLoginInput(username, password, vcode, true);
	}

	private void checkLoginInput(String username, String password) {
		checkLoginInput(username, password, null, false);
	}

	private void checkLoginInput(String phone, String password, String vcode, boolean checkvcode) {
		Assert.state(StringUtils.isNotBlank(phone), "请输入账号");
		Assert.state(StringUtils.isNotBlank(password), "请输入密码");
		// Assert.state(StringUtil.isPhoneNumber(phone), "手机号码错误");
		if (checkvcode) {
			boolean checked = getSession().getAttribute(CaptchaController.LOGIN_CAPTCHA) != null;
			String sessionCode = (String) getSession().getAttribute(CaptchaController.VERIFICATION_CODE);
			checked = checked || vcode != null && vcode.equalsIgnoreCase(sessionCode);
			Assert.state(checked, "验证码错误");
		}
		getSession().removeAttribute(CaptchaController.VERIFICATION_CODE);
		getSession().removeAttribute(CaptchaController.LOGIN_CAPTCHA);
	}

	@RequestMapping("/front/user/getDirect")
	public View getDirect() {
		User user = getSessionUser();
		Page<User> page = userService.getChrildrenPage(user, getPageRequestMap());
		return new JsonVO(page);
	}

	@RequestMapping("islogin/admin")
	public View isAdminLogin() {
		return new JsonMap().add("islogin", getSessionAdmin() != null);
	}

	@RequestMapping("islogin/front")
	public View isFrongLogin() {
		return new JsonMap().add("islogin", getSessionUser() != null);
	}

	@PostMapping("/admin/admin/add")
	public View adminAdd(String username) {
		Assert.state(StringUtil.notEmpty(username), "请输入ID编号");
		User newAdmin = userService.findByUsername(username);
		Assert.notNull(newAdmin, "用户不存在");
		newAdmin.setRole(1);
		userService.save(newAdmin);
		return new JsonMap("设置成功");
	}

	@PostMapping("/admin/admin/delete")
	public View adminDel(Integer userId) {
		User delAdmin = userService.getOne(userId);
		Assert.notNull(delAdmin, "设置失败");
		Assert.state(delAdmin.getId() != User.ROOT_USER_ID, "不能删除主账号权限");
		delAdmin.setRole(0);
		userService.save(delAdmin);
		return new JsonMap("设置成功");
	}

	@GetMapping("/front/user/myChart/child")
	public View myChartChild() {
		User user = getSessionUser();
		Collection<Integer> childrenIds = userTreeService.getChildrenIds(user.getId());
		List<User> children = userService.findAllById(childrenIds);

		ArrayList<User> loadLevel = new ArrayList<>(children);
		loadLevel.add(user);
		userLevelService.loadUscLevel(loadLevel);

		@Getter
		class Data {
			String name;
			List<Data> children;

			Data(User u) {
				name = u.getUsername() + "\nV" + u.getLevel() + "\n" + MiningMachine.names[u.getMiningType()];
			}

		}

		Data data = new Data(user);
		List<Data> c = convert(children, u -> new Data(u));
		data.children = c;

		return new JsonMap().add("resultdata", Arrays.asList(data));
	}

	@PostMapping("/front/mining/update")
	public View updateMining(Integer miningType) {
		checkSecondPwd();
		settlementService.updateMining(getSessionUser(), miningType);
		return new JsonMap("升级成功");
	}

	/**
	 * 后台升级或降级等级
	 *
	 * @param Levl
	 *            等级
	 * @param userId
	 *            用户id
	 * @param agent
	 *            0手动 1自动
	 * @return
	 */
	@RequestMapping("/admin/user/levelup")
	public View upAdminLevel(Integer agent, Integer level, Integer userId) {
		userService.upDownLevel(agent, level, userId);
		char aa= ' ';
		return new JsonMap("修改成功");
	}
}
