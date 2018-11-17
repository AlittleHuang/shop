package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.domain.Contact;
import com.shengchuang.member.additional.domain.Ore;
import com.shengchuang.member.additional.service.ContactService;
import com.shengchuang.member.additional.service.OreService;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.StreamUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class ContactController extends AbstractController {

    @Autowired
    private ContactService contactService;
    @Autowired
    private OreService oreService;

    @PostMapping("/front/contact/add")
    public JsonMap addContact(Contact contact, String password) {
        User user = getSessionUser();
        contact.setUserId(user.getId());
        contactService.add(contact, password);
        return new JsonMap("添加成功");
    }

    @RequestMapping("/front/contact/delete")
    public JsonMap deleteContact(Integer id) {
        Contact contact = contactService.getOne(id);
        Assert.notNull(contact, "记录不存在");
        Assert.state(contact.getUserId().equals(getSessionUser().getId()), "记录不存在");
        contactService.deleteById(id);
        return new JsonMap("删除成功");
    }

    @RequestMapping("/front/contact/list")
    public JsonVO deleteContact() {
        PageRequestMap pageRequestMap = getPageRequestMap();
        Page<Contact> page = contactService.getPage(getSessionUser().getId(), pageRequestMap);

        userService.loadUser(page.getContent(), Contact::getContactId, Contact::setContact);
        List<User> users = StreamUtil.convert(page.getContent(), it -> it.getContact());

        List<Ore> list = oreService.createCriteria().andIn("userId", User.idList(users))
                .andEqual("date", LocalDate.now())
                .getList();

        Map<Integer, User> userMap = User.mapById(users);
        for (Ore ore : list) {
            User user = userMap.get(ore.getUserId());
            user.data = ore.getTotal();
        }

        return new JsonVO(page);
    }

    @RequestMapping("/front/user/contact/switch")
    public JsonMap switchLinkedUser(Integer id) {
        Assert.notNull(id, "参数错误");
        Contact contact = contactService.getOne(id);
        Assert.notNull(contact, "id错误");
        User user = getSessionUser();
        Assert.state(contact.getUserId().equals(user.getId()), "id不匹配");
        setSessionUser(contact.getContactId());
        return new JsonMap();
    }

}
