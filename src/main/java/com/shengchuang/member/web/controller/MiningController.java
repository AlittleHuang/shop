package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.domain.MiningMachine;
import com.shengchuang.member.additional.domain.Ore;
import com.shengchuang.member.additional.service.MiningMachineService;
import com.shengchuang.member.additional.service.OreService;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.BalanceLog;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.service.UserTreeService;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import java.util.List;

@Controller
public class MiningController extends AbstractController {

    @Autowired
    private MiningMachineService miningMachineService;
    @Autowired
    private UserTreeService userTreeService;
    @Autowired
    private OreService oreService;

    @RequestMapping("/front/mining-machine/team/info")
    public View getTeamValueTotal() {
        User user = getSessionUser();
        List<Integer> teamIds = userTreeService.getTeamIds(user.getId(), false);
        long count = 0;
        Double sum =0.0;
        if(teamIds.size()>0) {
        	Criteria<MiningMachine> criteria = miningMachineService.createCriteria()
                       .andIn("userId", teamIds)
                       .andEqual("status", 0);
        	count = criteria.count();
        	sum = (Double) criteria
                    .addSelectSum("amount")
                    .getOneObject();
        }
        return new JsonMap().add("sum", sum == null ? 0 : sum).add("count", count);
    }

    @RequestMapping("/front/mining-machine/team/new/info")
    public View getNewSum() {
        User user = getSessionUser();
        List<Integer> teamIds = userTreeService.getTeamIds(user.getId(), false);

        Criteria<MiningMachine> criteria = miningMachineService.createCriteria()
                .andIn("userId", teamIds)
                // .andEqual("status", 0)
                .andGe("createTime", TimeUtil.getStartTimeToday());
        long count = criteria.count();
        Double sum = (Double) criteria
                .addSelectSum("amount")
                .getOneObject();

        return new JsonMap().add("sum", sum == null ? 0 : sum).add("count", count);
    }

    @RequestMapping("/front/mining-machine/info")
    public View getMiningMachine() {
        Integer userId = getSessionUser().getId();
        MiningMachine one = miningMachineService.createCriteria()
                .andEqual("userId", userId)
                .addOrderByDesc("id")
                .limit(1)
                .getOne();
        Ore ore = oreService.getByCurrDate(userId);

        long MillMaxDay = (one.getEndTime().getTime() - one.getCreateTime().getTime()) / TimeUtil.MILLIS_PER_DAY;
        long day = (System.currentTimeMillis() - one.getCreateTime().getTime()) / TimeUtil.MILLIS_PER_DAY;
        //个人总算力
        // double totalMining = balanceLogService.findSumAmount(getSessionUser().getId(), Event.MINING);
        return new JsonMap()
                .add("MillMaxDay", MillMaxDay)
                .add("day", MillMaxDay - day)
                .add("content", one)
                // .add("totalMining", totalMining)
                .add("income", ore.getTotal());
    }


    @RequestMapping("/front/mining-machine/info_v2")
    public View getMiningMachineV2() {
        Integer userId = getSessionUser().getId();
        MiningMachine one = miningMachineService.createCriteria()
                .andEqual("userId", userId)
                .addOrderByDesc("id")
                .limit(1)
                .getOne();

        long millMaxDay = (one.getEndTime().getTime() - one.getCreateTime().getTime()) / TimeUtil.MILLIS_PER_DAY;
        long day = (System.currentTimeMillis() - one.getCreateTime().getTime()) / TimeUtil.MILLIS_PER_DAY;
        //个人总算力
        double totalMining = balanceLogService.findSumAmount(getSessionUser().getId(), Event.MINING);
        Criteria<BalanceLog> criteria = balanceLogService.createCriteria()
                .andEqual("userId", userId)
                .andEqual("operation", Event.MINING.index)
                .andGe("time", TimeUtil.getStartTimeToday());
        double sum = balanceLogService.sum(criteria);
        return new JsonMap()
                .add("millMaxDay", millMaxDay)//有效期
                .add("day", millMaxDay - day)// 剩余天数
                .add("miningMachine", one) //
                .add("totalMining", totalMining) // 挖矿总量
                .add("income", sum);// 今日挖矿收入
    }


    /**
     * 个人矿机信息
     */
    @RequestMapping("/front/miningMachine/details")
    public View miningMachineDetails() {
        MiningMachine one = miningMachineService.createCriteria()
                .andEqual("userId", getSessionUser().getId())
                .addOrderByDesc("id")
                .limit(1)
                .getOne();
        return new JsonMap().add("content", one);
    }
}
