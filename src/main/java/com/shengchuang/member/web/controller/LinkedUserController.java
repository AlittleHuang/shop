package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.domain.LinkedUser;
import com.shengchuang.member.additional.service.LinkedUserService;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
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

@Controller
public class LinkedUserController extends AbstractController {

    @Autowired
    private LinkedUserService linkedUserService;

    @PostMapping("front/user/linked/add")
    public JsonMap addLinkedUser(LinkedUser linkedUser) {
        User user = getSessionUser();
        Integer userId = user.getId();
        Assert.state(!user.getUsername().equals(linkedUser.getUsername()), "用户名错误,不能关联自己");
        Assert.state((int) user.getId() != linkedUser.getLinkedId(), "会员ID错误,不能关联自己");
        linkedUser.setUserId(userId);
        linkedUserService.add(linkedUser);
        return new JsonMap("添加成功");
    }

    @RequestMapping("/front/user/linked/switch")
    public JsonMap switchLinkedUser(Integer id) {
        Assert.notNull(id, "参数错误");
        LinkedUser linkedUser = linkedUserService.getOne(id);
        Assert.notNull(linkedUser, "id错误");
        User user = getSessionUser();
        Assert.state(linkedUser.getUserId().equals(user.getId()), "id不匹配");
        setSessionUser(linkedUser.getLinkedId());
        return new JsonMap();
    }

    @RequestMapping("/front/user/linked/delete")
    public JsonMap delete(Integer id) {
        Assert.notNull(id, "参数错误");
        linkedUserService.delete2Way(id);
        return new JsonMap("删除成功");
    }

    @RequestMapping("/front/user/linked/list")
    public JsonVO list() {
        User user = getSessionUser();
        PageRequestMap pageRequest = getPageRequestMap();
        Page<LinkedUser> page = linkedUserService.getPage(user.getId(), pageRequest);
        linkedUserService.loadUsername(page);
        return new JsonVO(page);
    }

}
