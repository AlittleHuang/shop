package com.shengchuang.member.web.controller;

import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.member.core.domain.BalanceLog;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.base.AbstractController;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

@RestController
public class BalanceLogController extends AbstractController {

    /**
     * 财务明细
     *
     * @return
     */
    @RequestMapping("/admin/balance/log")
    public View balanceLog() {
        Page<BalanceLog> page = balanceLogService.getPage((User) null, getPageRequestMap());
        balanceLogService.loadUsername(page.getContent());
        return new JsonVO(page);
    }


    /**
     * 财务明细
     */
    @RequestMapping("/front/balance/log")
    public View frontBalanceLog() {
        User user = getSessionUser();
        Page<BalanceLog> page = balanceLogService.getPage(user, getPageRequestMap());
        userService.loadUser(page.getContent(), BalanceLog::getUserId, BalanceLog::setUser);
        return new JsonVO(page);
    }

    @RequestMapping("/admin/balance/log/inv/today")
    public View TotalInvToday() {
        Object sum = balanceLogService.createCriteria()
                .andEqual("operation", Event.ADD_MINING.index)
                .andGt("amount", 0.0)
                .addSelectSum("amount")
                .getOneObject();
        return new JsonMap().add("content", sum == null ? 0.0 : sum);
    }

}
