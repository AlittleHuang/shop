package com.shengchuang.member.core.service;

import com.shengchuang.common.exception.BusinessException;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.BalanceLog;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.repository.BalanceRepository;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BaseBalanceService extends AbstractService<Balance, Integer> {

    @Autowired
    protected UserService userService;

    @Autowired
    protected BalanceLogService balanceLogService;

    @Autowired
    protected BalanceRepository balanceDao;

    /**
     * 计算收入并保存记录
     *
     * @param balance 钱包
     * @param amount  金额
     * @param event   操作类型
     * @param info    明细备注
     * @return
     * @throws BusinessException amount<0
     */
    @Transactional
    public BalanceLog incomeAndLog(Balance balance, double amount, Event event, String info) {
        Assert.state(amount >= 0, "income amount must be greater than 0");
        return settlementAndLog(balance, amount, event, info).log;
    }

    @Override
    public Criteria<Balance> createCriteria() {
        return super.createCriteria().andNotEqual("type", BalanceType.INV.index);
    }

    /**
     * 计算支出并保存记录
     *
     * @param balance 钱包
     * @param amount  金额
     * @param event   事件类型
     * @param info    明细备注
     * @return
     * @throws BusinessException 余额不足
     */
    @Transactional
    public BalanceLog expenditureAndLog(Balance balance, double amount, Event event, String info) {
        Assert.state(amount >= 0, " expenditure amount must be greater than 0");
        return settlementAndLog(balance, -amount, event, info, true).log;
    }


    @Transactional
    public R settlementAndLog(Balance balance,
                              double amount,
                              Event event,
                              String info) {
        return settlementAndLog(balance, amount, event, info, true);
    }

    @Transactional
    public R settlementAndLog(Balance balance,
                              double amount,
                              Event event,
                              String info,
                              boolean saveLogNow)
            throws BusinessException {
        if (Math.abs(amount) < Balance.MIN_CHANGE / 10) {
            return new R(merge(balance), null);
        }
        Double currentBalance = balance.getAmount() + amount;
        if (amount < 0) {
            Assert.isTrue(currentBalance >= 0, "余额不足");
            Double lockedAmount = balance.getLockedAmount();
            if (lockedAmount != null) {
                Assert.isTrue(currentBalance >= lockedAmount, "可用余额不足");
            }
        }
        amount = NumberUtil.halfUp(amount, 6);
        balance.setAmount(currentBalance);
        BalanceLog log = new BalanceLog(balance.getUserId(), balance.getType(), amount,
                NumberUtil.halfUp(currentBalance, 6), event.getIndex(), info);
        if (saveLogNow)
            balanceLogService.save(log);
        return new R(merge(balance), log);
    }

    public class R {
        public final Balance balance;
        public final BalanceLog log;

        public R(Balance balance, BalanceLog log) {
            this.balance = balance;
            this.log = log;
        }
    }

}
