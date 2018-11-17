package com.shengchuang.member.core.service;

import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.Recharge;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.Withdraw;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class WithdrawService extends AbstractService<Withdraw, Integer> {

    @Autowired
    private UserService userService;

    @Autowired
    private BalanceService balanceService;

    /**
     * 提现申请
     *
     * @param withdraw 提现订单
     * @return
     */
    @Transactional
    public void withdraw(Withdraw withdraw) {

        Balance balance = balanceService.findOrCreateByUserIdAndType(withdraw.getUserId(), withdraw.getBalanceType());
        Double withdrawFee = withdraw.getFeerate();//提现手续费
        Double amount = withdraw.getAmount();
        double chargeAmount = amount * withdrawFee;
        amount += amount * withdrawFee;
        Assert.state(balance.getAmount() >= amount, "余额不足");
        balanceService.expenditureAndLog(balance, amount, Event.WITHDRAW, "提现申请:" + withdraw.getAmount() + "," +
                "扣除手续费:" + NumberUtil.fix2(chargeAmount));
        balanceService.save(balance);

        withdraw.setAmount(withdraw.getAmount());
        save(withdraw);
    }

    public Page<Withdraw> getPage(int userId, PageRequestMap pageRequestMap) {
        Criteria<Withdraw> conditions = createCriteria(pageRequestMap);
        conditions.andEqual("userId", userId);
        conditions.addOrderByDesc("updateTime", "createTime");
        Page<Withdraw> page = getPage(conditions);
        return page;
    }

    public Page<Withdraw> getPage(PageRequestMap pageRequestMap) {
        Criteria<Withdraw> conditions = createCriteria(pageRequestMap);
        Criteria<Withdraw> criteria = conditions;
        conditions.addOrderByDesc("updateTime", "createTime");
        String username = pageRequestMap.get("username");

        userService.addUsernameFilter(criteria, username);

        String balanceType = pageRequestMap.getStringValue("balanceType");
        if (balanceType != null) {
            criteria.andEqual("payInfo.displayType", balanceType);
        }
        Date startTime = pageRequestMap.getDateValue("startTime");
        Date endTime = pageRequestMap.getDateValue("endTime");
        if (startTime != null) {
            criteria.andGe("createTime", startTime, Date.class);
        }
        if (endTime != null) {
            endTime = TimeUtil.addDay(endTime, 1);
            criteria.andLt("createTime", endTime, Date.class);
        }
        Page<Withdraw> page = getPage(conditions);
        return page;
    }

    public void loadUsername(List<Withdraw> content) {
        if (content == null)
            return;
        for (Withdraw withdraw : content) {
            User one = userService.getOne(withdraw.getUserId());
            withdraw.setUsername(one.getUsername());
        }
    }

    @Transactional
    public void agree(Integer id) {
        audit(id, true);
    }

    @Transactional
    public void disagree(Integer id) {
        audit(id, false);
    }

    /**
     * 审核
     *
     * @param id
     * @param status true通过,false 不通过
     */
    @Transactional
    public void audit(Integer id, boolean status) {
        Withdraw withdraw = getOne(id);
        Assert.notNull(withdraw, "记录不存在");
        withdraw.setUpdateTime(new Date());
        Assert.state(Withdraw.STATUS_0 == withdraw.getStatus(), "已审核过");
        withdraw.setStatus(status ? Recharge.STATUS_1 : Recharge.STATUS_2);
        Balance balance = balanceService.findOrCreateByUserIdAndType(withdraw.getUserId(), withdraw.getBalanceType());
        save(withdraw);
        if (!status) { // 拒绝
            double backAmount = withdraw.getAmount() + withdraw.getFeerate() * withdraw.getAmount();
            balanceService.incomeAndLog(balance, backAmount, Event.WITHDRAW, "提现被拒绝,返还金额" + backAmount);
        }
    }

    public List<Withdraw> getWithdraw(User user, PageRequestMap pageRequestMap) {
        Criteria<Withdraw> conditions = createCriteria(pageRequestMap);
        Criteria<Withdraw> criteria = conditions;
        conditions.addOrderByDesc("updateTime", "createTime");
        userService.addUserFilter(user, pageRequestMap, criteria);
        String balanceType = pageRequestMap.getStringValue("balanceType");
        if (balanceType != null && !balanceType.equals("")) {
            criteria.andEqual("payInfo.displayType", balanceType);
        }
        Date startTime = pageRequestMap.getDateValue("startTime");
        Date endTime = pageRequestMap.getDateValue("endTime");
        if (startTime != null) {
            criteria.andGe("createTime", startTime, Date.class);
        }
        if (endTime != null) {
            endTime = TimeUtil.addDay(endTime, 1);
            criteria.andLt("createTime", endTime, Date.class);
        }
        List findWithdrawList = findObjList(conditions);
        return findWithdrawList;
    }

}
