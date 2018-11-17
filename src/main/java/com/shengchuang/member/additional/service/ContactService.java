package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.Contact;
import com.shengchuang.member.additional.repository.ContactRepository;
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

import java.util.Collection;

@Service
public class ContactService extends AbstractService<Contact, Integer> {

    @Autowired
    private UserService userService;
    @Autowired
    private ContactRepository ContactDao;

    public void add(Contact contact,  String password) {
        Assert.notEmpty(contact.getUser().getUsername(), "请填写用户名");
        User target = userService.findByUsername(contact.getUser().getUsername());
        Assert.notNull(target, "用户不存在");

        Assert.state(target.getPassword().equals(PasswordUtil.encodeStringPassword(password, target.getUsername())),
                "密码错误");

        Assert.state(!target.getId().equals(contact.getUserId()), "不能添加自己");
        boolean exists = existsByUserIdAndContactId(contact.getUserId(), target.getId());

        Assert.state(!exists, "联系人已添加");
        contact.setContactId(target.getId());
        contact.setContact(target);
        save(contact);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public <S extends Contact> S save(S entity) {
        boolean exists = existsByUserIdAndContactId(entity.getUserId(), entity.getContactId());
        Assert.state(!exists, "联系人已添加");
        return super.save(entity);
    }

    private boolean existsByUserIdAndContactId(int userId, int contactID) {
        return ContactDao.existsByUserIdAndContactId(userId, contactID);
    }

    public Page<Contact> getPage(Integer id, PageRequestMap pageRequestMap) {
        Criteria<Contact> conditions = createCriteria(pageRequestMap);
        Criteria<Contact> criteria = conditions;
        if (id != null) {
            criteria.andEqual("userId", id);
        }
        Page<Contact> page = getPage(conditions);
        return page;
    }

    public void loadUsername(Collection<Contact> contacts) {
        userService.loadUser(
                contacts,
                (contact) -> contact.getContactId(),
                (contact, user) -> contact.setUser(user)
        );
    }

}
