package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.domain.CustomerMessage;
import com.shengchuang.member.additional.service.CustomerMessageService;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CustomerMessageController extends AbstractController {

    @Autowired
    private CustomerMessageService customerMessageService;

    @PostMapping("/front/customer/message/add")
    public View add(CustomerMessage customerMessage) {
        customerMessage.setUserId(getSessionUser().getId());
        customerMessage.setRecipientId(User.SYSTEM_ID);
        customerMessageService.add(customerMessage);
        return new JsonMap("保存成功");
    }

    @PostMapping("/admin/customer/message/add")
    public View addByAdmin(CustomerMessage customerMessage, String username) {
        Assert.notEmpty(username, "请输入收件人");
        Assert.notEmpty(customerMessage.getTitle(), "请输入标题");
        Assert.notEmpty(customerMessage.getContent(), "请输入邮件内容");
        User user = userService.findByUsername(username);
        Assert.notNull(user, "用户名不存在");
        customerMessage.setUserId(User.SYSTEM_ID);
        customerMessage.setRecipientId(user.getId());
        customerMessageService.add(customerMessage);
        return new JsonMap("保存成功");
    }

    @RequestMapping("/front/customer/message/list/send")
    public View listFront() {
        Page<CustomerMessage> page = customerMessageService.getPage(getSessionUser(), getPageRequestMap());
        List<CustomerMessage> list = page.getContent();
        return new JsonVO(list);
    }

    @RequestMapping("/pc/customer/message/list/send")
    public View listPc() {
        Page<CustomerMessage> page = customerMessageService.getPage(getSessionUser(), getPageRequestMap());
        return new JsonVO(page);
    }

    @RequestMapping("/front/customer/message/list/recieve")
    public View listFrontRecieve() {
        Page<CustomerMessage> page = customerMessageService.getPageRecieve(getSessionUser(), getPageRequestMap());
        List<CustomerMessage> list = page.getContent();
        return new JsonVO(list);
    }

    @RequestMapping("/pc/customer/message/list/recieve")
    public View listPcRecieve() {
        Page<CustomerMessage> page = customerMessageService.getPageRecieve(getSessionUser(), getPageRequestMap());
        return new JsonVO(page);
    }

    @RequestMapping("/admin/customer/message/list/reciece")
    public View listAdminRecieve() {
        Page<CustomerMessage> page = customerMessageService.getPage((User) null, getPageRequestMap());
        customerMessageService.loadSendder(page.getContent());
        return new JsonVO(page);
    }

    @RequestMapping("/admin/customer/message/list/send")
    public View listAdminSend() {
        Page<CustomerMessage> page = customerMessageService.getPageSystem(getPageRequestMap());
        customerMessageService.loadRecieve(page.getContent());
        return new JsonVO(page);
    }

    @RequestMapping("/front/customer/message/status/update")
    public View updateStatus(Integer id, Integer status) {
        Assert.state(status != null && 1 == status, "status -> 0");
        CustomerMessage one = customerMessageService.getOne(id);
        Assert.state(one != null && getSessionUser().getId().equals(one.getUserId()), "id 错误");
        one.setStatus(status);
        customerMessageService.save(one);
        return new JsonMap("操作成功");
    }

    @RequestMapping("/front/customer/message/personal")
    public View findById(Integer id, Integer opt) {
        Map<String, Object> map = new HashMap<>();
        CustomerMessage one = customerMessageService.getOne(id);
        User user;
        if (opt != null) {
            if (opt == 0) {
                user = userService.getOne(one.getUserId());
                map.put("name", "admin");
                map.put("username", user.getUsername());
            }
            if (opt == 1) {
                user = userService.getOne(one.getRecipientId());
                map.put("name", user.getUsername());
                map.put("username", "admin");
            }
        }
        map.put("title", one.getTitle());
        map.put("content", one.getContent());
        map.put("time", one.getCreateTime());
        map.put("type", one.getTypeName());
        return new JsonVO(map);
    }

    @PostMapping("/admin/customer/message/status/change")
    public View changeStatus(Integer id) {
        customerMessageService.changeStatus(id);
        return new JsonMap("操作成功");
    }

    /**
     * 统计消息总数
     *
     * @return
     */
    @RequestMapping("/admin/message/count")
    public View userCount() {
        Long countMessages = customerMessageService.getCountMessages();
        return new JsonVO(countMessages);

    }

}
