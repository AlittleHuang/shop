package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.service.StatisticalBalanceService;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.domain.util.BalancesIndex;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.List;

import static com.shengchuang.common.util.StreamUtil.*;

@RestController
public class StatisticalBalanceController extends AbstractController {

    @Autowired
    private StatisticalBalanceService statisticalBalanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private BalanceService balanceService;

    @RequestMapping("admin/user/balance/info")
    public View StatisticalBalance() {
//        Page<StatisticalBalance> page = statisticalBalanceService.getPage(getPageRequestMap());
        Page<User> page = userService.getPage(getPageRequestMap());
        List<Balance> list = balanceService.createCriteria()
                .andIn("userId", User.idList(page.getContent()))
                .andIn("type", convert(BalanceType.displays(),BalanceType::getIndex))
                .getList();
        BalancesIndex index = new BalancesIndex(list);
        for (User user : page) {
            user.data = index.getByUserId(user.getId()).stream()
                    .collect(mapCollector(Balance::type, Balance::getAmount));
        }
        return new JsonVO(page);
    }
}
