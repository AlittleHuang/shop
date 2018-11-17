package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.CustomerMessage;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class CustomerMessageService extends AbstractService<CustomerMessage, Integer> {

    @Autowired
    private UserService userService;

    public void add(CustomerMessage customerMessage) {
        Assert.isNull(customerMessage.getId(), "参数错误");
        Assert.notEmpty(customerMessage.getType(), "类型未填写");
        Assert.notEmpty(customerMessage.getTitle(), "标题未填写");
        Assert.notEmpty(customerMessage.getContent(), "内容未填写");
        Date now = new Date();
        customerMessage.setCreateTime(now);
        customerMessage.setUpdateTime(now);
        customerMessage.setStatus(0);
        save(customerMessage);
    }

    public Page<CustomerMessage> getPage(User user, PageRequestMap pageRequestMap) {
        Criteria<CustomerMessage> conditions = toPageConditions(pageRequestMap);
        conditions.addOrderByDesc("createTime");
        Criteria<CustomerMessage> criteria = conditions.andNotEqual("userId", User.SYSTEM_ID);
        if (user != null) {
            criteria.andEqual("userId", user.getId());
        }
        criteria.andEqual(pageRequestMap.asMap());
        return getPage(conditions);
    }

    /**
     * 获取系统发送的邮件
     *
     * @param pageRequestMap
     * @return
     */
    public Page<CustomerMessage> getPageSystem(PageRequestMap pageRequestMap) {
        Criteria<CustomerMessage> conditions = toPageConditions(pageRequestMap).addOrderByDesc("createTime")
                .and().andEqual("userId", User.SYSTEM_ID);
        userService.addFindByUsername(conditions, pageRequestMap);
        return getPage(conditions);
    }

    public void loadRecieve(Collection<CustomerMessage> customerMessages) {
        userService.loadUser(customerMessages, cm -> cm.getRecipientId(), (cm, user) -> cm.setUser(user));
    }

    public void loadSendder(Collection<CustomerMessage> customerMessages) {
        userService.loadUser(customerMessages, cm -> cm.getUserId(), (cm, user) -> cm.setSendder(user));
    }

    public Page<CustomerMessage> getPageRecieve(User user, PageRequestMap pageRequestMap) {
        Criteria<CustomerMessage> conditions = toPageConditions(pageRequestMap).addOrderByDesc("createTime")
                .and().andEqual("recipientId", user.getId());
        userService.addFindByUsername(conditions, pageRequestMap);
        return getPage(conditions);
    }

    public void changeStatus(int id) {
        CustomerMessage one = getOne(id);
        if (one.getStatus() == null) {
            List<CustomerMessage> status = createCriteria().andIsNull("status").getList();
            for (CustomerMessage message : status) {
                message.setStatus(0);
            }
            saveAll(status);
        }
        one.setStatus(Math.abs(one.getStatus() - 1));
        save(one);
    }

    /**
     * 统计总人数
     */
    public Long getCountMessages() {
        Criteria<CustomerMessage> criteria = createCriteria();
        return criteria.count();
    }

}
