package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.LinkedUser;
import com.shengchuang.member.additional.repository.LinkedUserRepository;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.base.AbstractService;
import com.shengchuang.member.core.shiro.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LinkedUserService extends AbstractService<LinkedUser, Integer> {

    @Autowired
    private LinkedUserRepository linkedUserDao;

    @Autowired
    private UserService userService;

    @Transactional
    public void delete2Way(int id) {
        LinkedUser one = getOne(id);
        if (one == null)
            return;
        linkedUserDao.delete(one);
        linkedUserDao.deleteByUserIdAndLinkedId(one.getLinkedId(), one.getUserId());
    }

    @Transactional
    public void add(LinkedUser linkedUser) {
        Integer linkedId = linkedUser.getLinkedId();
        Assert.notNull(linkedId, "未填写会员ID");
        String username = linkedUser.getUsername();
        String password = linkedUser.getPassword();
        String secondpwd = linkedUser.getSecondpwd();
        Assert.notNull(username, "未填写会员名");
        Assert.notNull(password, "未填写登录密码");
        Assert.notNull(secondpwd, "未填写交易密码");
        password = PasswordUtil.encodeStringPassword(password, username);
        secondpwd = PasswordUtil.encodeStringPassword(secondpwd, username);
        boolean expression = userService.existsByIdAndUsernameAndPasswordAndSecondpwd(linkedId, username, password,
                secondpwd);
        Assert.state(expression, "信息不匹配");
        LinkedUser linkedUserDb = linkedUserDao.findByUserIdAndLinkedId(linkedUser.getUserId(), linkedId);
        Assert.isNull(linkedUserDb, "已关联该用户");
        save(new LinkedUser(linkedId, linkedUser.getUserId()));
        save(linkedUser);
    }

    public Page<LinkedUser> getPage(Integer userId, PageRequestMap pageRequest) {
        Criteria<LinkedUser> conditions = createCriteria(pageRequest);
        Criteria<LinkedUser> criteria = conditions;
        if (userId != null) {
            criteria.andEqual("userId", userId);
        }
        return getPage(conditions);
    }

    public void loadUsername(Iterable<LinkedUser> page) {
        for (LinkedUser linkedUser : page) {
            if (linkedUser == null)
                continue;
            Integer linkedId = linkedUser.getLinkedId();
            if (linkedId == null)
                continue;
            User one = userService.getOne(linkedId);
            if (one == null)
                continue;
            linkedUser.setUsername(one.getUsername());
        }
    }

}
