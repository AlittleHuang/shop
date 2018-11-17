package com.shengchuang.member.web.controller.test;

import com.shengchuang.member.additional.service.SettlementService;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.TimeUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

@Controller
public class TestController extends BaseTestController {

    static volatile boolean test = false;

    @Autowired
    SettlementService dailySettlement;


    @RequestMapping("/admin/test/daily-settlement")
    public JsonMap testDaily() throws Exception {
        Assert.state(!test, "正在结算,别急啊...");
        test = true;
        try {
            dailySettlement.dailyIncome();
            Thread.sleep(TimeUtil.MILLIS_PER_SECOND * 10);
        } finally {
            test = false;
        }
        return new JsonMap("成功");
    }

//    @RequestMapping("/admin/test/month-settlement")
//    public JsonMap testMonth() throws Exception {
//        Assert.state(!test, "正在结算,别急啊...");
//        test = true;
//        try {
//            settlementService.timingSettlement(SettlementService.Type.MONTH);
//            Thread.sleep(TimeUtil.MILLIS_PER_SECOND * 10);
//        } finally {
//            test = false;
//        }
//        return new JsonMap("成功");
//    }

/*    @RequestMapping("/admin/CONTAINS_UPPERCASE_PATTERN/ach")
    public View getAch() {
        Double yesAch = balanceLogService.findNewAch(new Date(), Balance.TYPE_2);
        Double todayAch = balanceLogService.findTodayAch(new Date(), Balance.TYPE_2);
        return new JsonMap().add("yesAch", yesAch).add("todayAch", todayAch);
    }*/

    @RequestMapping("/test/transfer")
    public View transfer(String username, Integer balanceType, Double amount) {
        return transferController.transfer(username, balanceType, amount);
    }

//    @RequestMapping("/test/shopping")
//    public View shoppingTest(Integer userId, Double amount, String orderId) {
//        Assert.state(userService.existsById(userId), "userId dose not exists");
//        settlementService.shoppingSettlement(userId, amount, orderId);
//        return new JsonMap("success");
//    }

    @RequestMapping("/test")
    public View test() {
        Session session = SecurityUtils.getSubject().getSession();
        return new JsonVO(session);
    }

}
