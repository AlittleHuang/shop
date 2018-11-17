package com.shengchuang.member.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.member.additional.service.setting.domain.Settings;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.repository.UserRepository;
import com.shengchuang.member.core.shiro.PasswordUtil;
import com.shengchuang.member.web.vo.Ztree;
@Service
public class UserService extends BaseUserService {

    private static final ThreadLocal<Integer> side = new ThreadLocal<>();
    @Autowired
    private PayInfoService payInfoService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    UserTreeService userTreeService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private UserRepository userRepository;


    /**
     * 会员激活
     *
     * @param userId
     * @param level       特权积分
     * @param frontUserId 激活积分
     * @return
     */
    @Transactional
    public void active(Integer userId, Integer level, Integer frontUserId) {
        synchronized (("ACTIVE_ID:" + userId).intern()) {
            User beActived = getOne(userId);
            Assert.notNull(beActived, "id错误");
            Assert.state(beActived.getLevel() == 0, "用户已激活");
            //checkSide(beActived);
            logger.info("激活会员:" + beActived);
            beActived.setLevel(level);
            beActived.setActiveTime(new Date());
            balanceService.initUsersBalance(userId);//创建币种
            save(beActived);
            logger.info("激活会员:" + beActived + "成功");
        }
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    public User login(String username, String password) {
        User user = findByUsername(username);
        Assert.notNull(user, "账号或密码错误");
        password = PasswordUtil.encodeStringPassword(password, user.getUsername());
        Assert.state(password.equals(user.getPassword()), "账号或密码错误");
        return user;
    }

    /**
     * 注册
     */
    @Transactional
    public User register(User user) {
        Assert.notNull(user, "");

        String username = user.getUsername();
        Assert.hasText(username, "请输入用户名");
       // Pattern usernameTest = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9_]{5,15}");
//        Assert.state(usernameTest.matcher(username).matches(),
//                "用户名由6~16位字母数字下划线组成");
        Assert.state(!userDao.existsByUsername(username), "用户名已存在");

        String phone = user.getPhone();
        Assert.hasText(phone, "请输入手机号");
        /*Assert.state(StringUtil.isPhoneNumber(phone), "请输入正确的手机号");*/
        String pUsername = user.getReferrer().getUsername();
        Assert.notNull(isNotBlank(pUsername), "未填写推荐人ID编号");
        User pUser = findByUsername(pUsername);
        Assert.notNull(pUser, "推荐人不存在");
        user.setReferrer(pUser);
        user.setReferrerId(pUser.getId());
        Settings settings=settingsService.getSettings();
        int phoneCount = settings.getPhoneCount();
        synchronized (username.intern()) {
        	Assert.state(userDao.countByPhone(phone) < phoneCount, "您的手机号码绑定次数已达到上限");
           // Assert.state(userDao.countByPhone(phone) == 0, "手机号码已注册");
            Date now = new Date();
            user.setRegistTime(now);
            user.setActiveTime(now);
            user.setRole(0);
            user.setAgent(User.AGENT_ZD);
            encodePassword(user);
            user = saveAndFlush(user);
            balanceService.initUsersBalance(user.getId());//创建币种
        }
        return user;
    }

    /**
     * 修改登录密码
     */
    @Transactional
    public void updateLoginPassword(Integer userId, String password, String newPassword) {
        User user = getOne(userId);
        String username = user.getUsername();
        password = PasswordUtil.encodeStringPassword(password, username);
        String passwordDb = user.getPassword();
        Assert.state(password.equals(passwordDb), "原密码错误");
        Assert.state(newPassword.length() >= 6, "新密码至少六位数");
        user.setPassword(PasswordUtil.encodeStringPassword(newPassword, username));
        save(user);
    }
    
    /**
     * 直推的人
     */
    public Page<User> getChrildrenPage(User user, PageRequestMap pageRequestMap) {
        Criteria<User> conditions = toConditions(pageRequestMap);
        conditions.addOrderByDesc("registTime");
        Criteria<User> criteria = conditions;
        Date date = pageRequestMap.getDateValue("date");
        if (date != null) {
            criteria.andBetween("registTime", TimeUtil.getStartTimeOfDate(date), TimeUtil.getOverTimeOfDate(date));
        }
        criteria.andEqual("referrerId", user.getId());
        Page<User> page = getPage(conditions);
        return page;
    }

    /**
     * 未激活列表
     */
    public Page<User> getActivePage(User user, PageRequestMap pageRequestMap) {
        Criteria<User> conditions = toConditions(pageRequestMap);
        conditions.addOrderByDesc("registTime");
        conditions.andEqual("status", 0);
        Page<User> page = getPage(conditions);
        return page;
    }

    /**
     * 会员列表
     *
     * @param pageRequestMap
     * @return
     */
    public Page<User> getPage(PageRequestMap pageRequestMap) {
        Criteria<User> conditions = toPageConditions(pageRequestMap);
        Criteria<User> criteria = conditions;
        conditions.addOrderByDesc("registTime");
        criteria.andEqual(pageRequestMap.asMap());
        if (isNotBlank(pageRequestMap.get("referrer.username"))) {
            User puser = findByUsername(pageRequestMap.get("referrer.username"));
            if (puser == null) {
                return emptyPage();
            }
            criteria.andEqIgnoreEmpty("referrerId", puser.getId());
        }
        if (isNotBlank(pageRequestMap.get("ruser.username"))) {
            User ruser = findByUsername(pageRequestMap.get("ruser.username"));
            if (ruser == null) {
                return emptyPage();
            }
            criteria.andEqIgnoreEmpty("rid", ruser.getId());
        }
        return getPage(conditions);
    }

    private Criteria<User> toConditions(PageRequestMap pageRequestMap) {
        Criteria<User> conditions = super.createCriteria(pageRequestMap);
        Criteria<User> criteria = conditions;
        String level = pageRequestMap.get("level");
        if (level != null) {
            criteria.andEqual("level", level);
        }
        return conditions;
    }

    /**
     * 获取所有团队成员(不包括自己)
     *
     * @param user
     * @return
     */
    public List<User> getTeamList(User user) {
        List<Integer> teamIdsP = userTreeService.getTeamIds(user.getId(), false);
        List<User> list = findAllById(teamIdsP);
        return list;
    }

    /**
     * 升级用户
     *
     * @param userId
     * @param level
     */
    @Transactional
    public void upLevel(int userId, Integer level) {
        User user = findById(userId).orElse(null);
        Assert.notNull(user.getUsername(), "用户不存在");
        Assert.state(user.getLevel() < level && level <= User.MAX_LEVEL, "等级错误");
        logger.info("用户" + user + "升级");
        logger.info("用户" + user + "升级成功");
    }

    /**
     * 加载直推人的username(User.referrer.username)
     *
     * @param user
     */
    public void loadPusername(User user) {
        if (user.getReferrerId() != null) {
            User pUser = findById(user.getReferrerId()).orElse(new User());
            User u = new User();
            u.setUsername(pUser.getUsername());
            user.setReferrer(u);
        }
    }

    /**
     * 批量加载直推人的username(User.referrer.username)
     *
     * @param users
     */
    public void loadPusername(Collection<User> users) {
        loadUpUsername(users);
    }


    public void loadUpUsername(Collection<User> users) {
        loadUser(users, User::getReferrerId,
                (user, upuser) -> user.setReferrer(upuser == null ? null : new User(upuser.getUsername()))
        );
    }

    /**
     * 冻结用户
     *
     * @param userId
     */
    @Transactional
    public void disable(Integer userId) {
        User user = findById(userId).orElse(null);
        Assert.notNull(user, "用户不存在");
        Assert.state(user.getId() != User.ROOT_USER_ID, "不能冻结主账号");
        user.setFreeze((User.FREEZE_DJ));
    }

    /**
     * 解冻用户
     *
     * @param userId
     */
    @Transactional
    public void setAble(Integer userId) {
        User user = getOne(userId);
        Assert.notNull(user, "用户不存在");
        Assert.state(user.getFreeze() == User.FREEZE_DJ, "用户未被冻结");
        user.setFreeze(User.FREEZE_JD);
    }


    public List<Ztree> toZtree(List<User> users, List<Integer> userIds) {

        ArrayList<Ztree> list = new ArrayList<>();
        Map<Integer, User> uMap = users.stream().collect(Collectors.toMap(u -> u.getId(), u -> u));

        for (User user : users) {
            Ztree ztree = new Ztree();
            ztree.setId(String.valueOf(user.getId()));
            ztree.setPId(String.valueOf(user.getReferrerId()));
            StringBuilder sb = new StringBuilder();
            sb.append(user.getUsername());

            ztree.setName(sb.toString());
            list.add(ztree);
        }
        return list;
    }

    public List<User> findByPhoneAndIdNotIn(String phone, Integer[] userIds) {
        return userDao.findByPhoneAndIdNotIn(phone, userIds);
    }

    public List<User> getUserList(User user, PageRequestMap pageRequestMap) {
        Criteria<User> conditions = toPageConditions(pageRequestMap);
        conditions.addOrderByDesc("registTime");
        Criteria<User> criteria = conditions;
        if (isNotBlank(pageRequestMap.get("referrer"))) {
            User puser = findByUsername(pageRequestMap.get("referrer"));
            if (puser != null) {
                criteria.andEqIgnoreEmpty("referrerId", puser.getId());
            }
        }
        List findUserList = findObjList(conditions);
        return findUserList;
    }

    public List<User> findByRegistTime(Date date) {
        List<User> userList = createCriteria().andBetween("registTime",TimeUtil.getStartTimeOfDate(date),TimeUtil.getOverTimeOfDate(date)).getList();
        return userList;
    }

    public double getGivingPowers(User user) {
        double givingPowers = 0.0;
        if (user != null) {
            if (user.getUsername() != null && user.getIdCard() != null) {
                givingPowers += 20.0;
            }
        }
        int count = payInfoService.countByUserId(user.getId());
        if (count > 0) {
            givingPowers += 20.0;
        }
        return givingPowers;
    }

    /**
     * 统计总人数
     */
    public Long getSumUsers() {
        return createCriteria().count();
    }

    /**
     * 统计昨天新增人数
     */
    public Long getNewAddUsers() {
        Criteria<User> criteria = createCriteria();
        Date date = new Date();
        criteria.andBetween("registTime", TimeUtil.getStartTimeOfDate(date), TimeUtil.getOverTimeOfDate(date));
        return criteria.count();
    }

    public void addUserFilter(User user, PageRequestMap pageRequestMap,
                              Criteria<?> criteria) {
        if (user != null) {
            criteria.andEqual("userId", user.getId());
        } else {
            String username = pageRequestMap.get("username");
            addUsernameFilter(criteria, username);
        }
    }

    public void addUsernameFilter(Criteria<?> criteria, String username) {
        if (isNotBlank(username)) {
            User find = findByUsername(username);
            if (find != null) {
                criteria.andEqual("userId", find.getId());
            } else {
                criteria.setPageResultEmpty();
            }
        }
    }

    /**
     * 直接修改登录密码
     * @param userId
     * @param password
     */
    @Transactional
    public void updatePwd(Integer userId, String password) {
        User user = getOne(userId);
        Assert.state(password.length() >= 6, "登录密码至少六位数");
        String username = user.getUsername();
        password = PasswordUtil.encodeStringPassword(password, username);
        user.setPassword(password);
        save(user);
    }
    /**
     * 直接修改交易密码
     * @param userId
     * @param password
     */
    @Transactional
    public void updateSecendpwd(Integer userId, String secendpwd) {
    	User user = getOne(userId);
    	Assert.state(secendpwd.length() >= 6, "交易密码至少六位数");
    	String username = user.getUsername();
    	secendpwd = PasswordUtil.encodeStringPassword(secendpwd, username);
    	user.setSecondpwd(secendpwd);
    	save(user);
    }

    @Transactional
	public void agree(User user) {
    	user.setIdStatus(User.IDSTATUS2);
    	save(user);
	}

    @Transactional
	public void disagree(User user) {
		user.setIdStatus(User.IDSTATUS3);
    	save(user);
	}

    @Transactional
    public void upDownLevel(Integer agent,Integer level, Integer userId) {
        Assert.notNull(level, "用户等级错误");
        User user = findById(userId).orElse(null);
        Assert.notNull(user.getUsername(), "用户不存在");
        if(agent==User.AGENT_SD){
        	user.setAdminLevel(level);
            user.setAgent(agent);
        }else{
            user.setAgent(agent);
        }
        save(user);
        logger.info("修改用户等级成功，" + user + "用户等级为："+User.getLevelName(level));
    }

	/**
     * 重置密码
     */
    @Transactional
    public void resetLoginPassword(Integer userId, String password,String secondpwd) {
        User user = getOne(userId);
        String username = user.getUsername();
        Assert.state(password.length() >= 6, "登录密码至少六位数");
        Assert.state(secondpwd.length() >= 6, "交易密码至少六位数");
        user.setPassword(PasswordUtil.encodeStringPassword(password, username));
        user.setSecondpwd(PasswordUtil.encodeStringPassword(secondpwd, secondpwd));
        save(user);
    }

	public User findByUsernameAndPhone(String username, String phone) {
		return userRepository.findByUsernameAndPhone(username,phone);
	}
}
