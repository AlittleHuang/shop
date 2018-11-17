package com.shengchuang.member.core.service;

import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.StringUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.repository.UserRepository;
import com.shengchuang.base.AbstractService;
import com.shengchuang.member.core.shiro.PasswordUtil;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseUserService extends AbstractService<User, Integer> {
    protected UserRepository userDao;

    @Autowired
    public void setUserDao(UserRepository userDao) {
        this.userDao = userDao;
        initFistUser(userDao);//注册第一个用户
    }

    private void initFistUser(UserRepository userDao) {
        long count = count();
        if (count == 0) {
            User user = new User();
            user.setId(User.ROOT_USER_ID);
            user.setUsername("admin");
            user.setPassword("123123");
            user.setSecondpwd("123123");
            user.setPhone("admin");
            Date now = new Date();
            user.setRegistTime(now);
            user.setActiveTime(now);
            user.setLevel(1);
            user.setRole(1);//管理员
            user.setStatus(1);
            user.setMiningType(1);
            encodePassword(user);//密码加密
            save(user);
        }
    }

    /**
     * 密码加密
     */
    public void encodePassword(User user) {
        String password = user.getPassword();
        ByteSource solt = PasswordUtil.getSolt(user);
        password = PasswordUtil.encodeStringPassword(password, solt);
        user.setPassword(password);
        String secondpwd = user.getSecondpwd();
        if (secondpwd != null) {
            secondpwd = PasswordUtil.encodeStringPassword(secondpwd, solt);
            user.setSecondpwd(secondpwd);
        }
    }


    public List<User> findByLevel(int levle) {
        return userDao.findByLevel(levle);
    }

    //---------------------------------------CRUD------------------------------------------------->

    @Transactional
    public void saveUserAndUserInfo(User user) {
        saveSelective(user);
    }

    public void loadUserInfo(User user) {
    }

    public void loadUserInfo(Iterable<User> users) {
    }

    public List<User> findByReferrerId(int pid) {
        return userDao.findByReferrerId(pid);
    }

    public boolean existsByIdAndUsernameAndPasswordAndSecondpwd(
            Integer id,
            String username,
            String password,
            String secondpwd) {
        return userDao.existsByIdAndUsernameAndPasswordAndSecondpwd(id, username, password, secondpwd);
    }

    public boolean existsByIdAndUsername(Integer id, String username) {
        return userDao.existsByIdAndUsername(id, username);
    }

    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public Integer findUserIdByUsername(String username) {
        if (isBlank(username)) return null;
        Criteria<User> conditions = createCriteria().addSelect("id").and().andEqual("username", username);
        Object oneObj = findOneObj(conditions);
        return oneObj == null ? null : Integer.valueOf(oneObj.toString());
    }

    public Criteria<?> addFindByUsername(Criteria<?> conditions, PageRequestMap pageRequestMap) {
        String username = pageRequestMap.get("user.username");
        if (StringUtil.isEmpty(username))
            return conditions;
        User user = findByUsername(username);
        if (user == null) {
            conditions.setPageResultEmpty();
        } else {
            conditions.and().andEqual("userId", user.getId());
        }
        return conditions;
    }


    public <C extends Collection<T>, T> C loadUser(C entitys, Function<T, Integer> getUserId, BiConsumer<T, User> setUser) {
        Map<Integer, User> map = new HashMap<>(/*(int) (entitys.size() * 1.8 + 1)*/);
        Set<Integer> keySet = entitys.stream().map(e -> getUserId.apply(e)).collect(Collectors.toSet());
        if (keySet.isEmpty()) return entitys;
        find(createCriteria().and().andIn("id", keySet))
                .forEach(user -> map.put(user.getId(), user));
        for (T entity : entitys) {
            if (getUserId.apply(entity) != null)
                setUser.accept(entity, map.get(getUserId.apply(entity)));
        }
        return entitys;
    }

    public User findByPhone(String phone) {
        return userDao.findByPhone(phone);
    }

}
