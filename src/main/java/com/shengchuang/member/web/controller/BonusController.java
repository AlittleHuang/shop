package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.domain.Bonus;
import com.shengchuang.member.additional.service.BonusService;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.List;

@RestController
public class BonusController extends AbstractController {

    @Autowired
    private BonusService bonusService;

    /**
     * 奖金明细
     *
     * @return
     */
    @RequestMapping("/admin/bonus/log")
    public View bonusLog() {
        Page<Bonus> page = bonusService.getPage((User) null, getPageRequestMap());
        bonusService.loadUsername(page.getContent());
        return new JsonVO(page);
    }

    @RequestMapping({"/admin/events/bonus", "/fronts/events/bonus"})
    public View bonusType() {
        return new JsonMap().addEnums("content", Event.BONUS_EVENTS);
    }

    @RequestMapping({"/admin/events", "/front/events"})
    public View events() {
        return new JsonMap().addEnums("content", Event.CAIWU_EVENTS);
    }

    @RequestMapping({"/admin/recharge/events"})
    public View rechargeEvents() {
        List<Event> rechargeEvents = Event.RECHARGE_EVENTS;
        return new JsonMap().addEnums("content", rechargeEvents);
    }

}
