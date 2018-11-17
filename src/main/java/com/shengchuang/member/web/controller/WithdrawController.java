package com.shengchuang.member.web.controller;

import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.member.core.domain.Withdraw;
import com.shengchuang.member.core.service.WithdrawService;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

@Controller
public class WithdrawController extends AbstractController {

    @Autowired
    private WithdrawService withdrawService;

    @RequestMapping("/front/withdraw/pay-info")
    public View lastPayInfo() {
        Withdraw trading = withdrawService.createCriteria().andEqual("userId", getSessionUser().getId())
                .addOrderByDesc("createTime").limit(1).getOne();
        JsonMap jsonMap = new JsonMap();
        if (trading != null) {
            jsonMap.add("payInfo", trading.getPayInfo());
        } else {
            jsonMap.success(false);
        }
        return jsonMap;
    }

}
