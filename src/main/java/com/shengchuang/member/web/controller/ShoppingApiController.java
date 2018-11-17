package com.shengchuang.member.web.controller;


import com.shengchuang.member.additional.service.MiningMachineService;
import com.shengchuang.member.additional.service.SettlementService;
import com.shengchuang.member.additional.service.UserLevelService;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.base.AbstractController;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

@Controller
public class ShoppingApiController extends AbstractController {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private MiningMachineService miningMachineService;

    @RequestMapping("/api/shopping/finish")
    public View finishShoping(Integer userId, Double money, String orderId, String token_shop, Integer machine) {
        User user = userService.getOne(userId);
        SimpleHash sh = new SimpleHash("MD5", user.getUsername() + User.TOKEN, null, 3);
        String token = sh.toString();
        Assert.state(token.equals(token_shop), "token_shop 错误");
//        MiningMachine m = miningMachineService.findByUserId(userId);
//        miningMachineService.save(m);
        return new JsonMap("success");
    }


    @RequestMapping("/api/shoping/balance/change")
    public View changeBalance(Integer userId, Double amount, int balanceTypa, String token) {
        // TODO: 2018/5/16  操作币种
        return new JsonMap();
    }

    @RequestMapping("/api/shoping/user/login/check")
    public View loginCheck(String username, String password, String token) {
        // TODO: 2018/5/16 登录校验
        return new JsonMap();
    }
}
