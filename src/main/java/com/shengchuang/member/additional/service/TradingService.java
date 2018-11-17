package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.service.setting.AbstractSettings.Key;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.PayInfo;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.service.*;
import com.shengchuang.base.AbstractService;
import com.shengchuang.member.trading.domain.TradeOrder;
import com.shengchuang.member.trading.domain.Trading;
import com.shengchuang.member.trading.repository.TradingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TradingService extends AbstractService<Trading, Integer> {

    /**
     * 保证金
     */
    public static final Key<Double> MARGIN = new Key<>(Double.class, "margin", 0.00);

    static final Key<Double> TRADE_EXCHANGE_RATE = new Key<>(Double.class, "tradeExchangeRate", 1.0);
    @Autowired
    BalanceLogService balanceLogService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private TradeOrderService tradeOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private PayInfoService payInfoService;
    @Autowired
    private TransferSettingsService transferSettingsService;
    @Autowired
    private TradingRepository tradingDao;

    /* */

    //    /**
//     * 挂卖
//     *
//     * @param trading
//     */
    /*
    @Transactional
    public void add(Trading trading) {
        Assert.notNull(trading, "参数错误");
        Integer type = trading.getType();
        Assert.notNull(type, "参数错误");
        Integer userId = trading.getUserId();
        Assert.notNull(userId, "参数错误");
        Double amount = trading.getAmount();
        Assert.notNull(amount, "请输入出售数量");
        Assert.state(amount > 0, "出售数量错误");
        PayInfo payInfo = payInfoService.findDefault(trading.getUserId());
        //trading.setTotalAmount(amount);

        Balance balance = balanceService.findOrCreateByUserIdAndType(userId, trading.getType());
        double fees = settingsService.getSettings().getEpFee() * amount;
        Assert.state(balance.getAmount() >= amount + fees, "余额不足");
        trading.setFees(fees);

        trading.setTime(new Date());
        trading.setPayInfo(payInfo);
        BalanceLog log = balanceService.expenditureAndLog(balance, amount + fees, BalanceLog.TRADE, "出售");
        trading.setLog(log);
        save(trading);
    }*/
    public double getRmbFactor() {
        Double mmdPrice = settingsService.getSettings().getMmdPrice();
        return NumberUtil.halfUp(mmdPrice, 4);
    }

    /**
     * 购买
     */
    @Transactional
    public void buy(int buyerId, int tradingId, double amount) {
        Assert.state(amount > 0, "参数错误");
        synchronized (String.valueOf(tradingId).intern()) {
            Trading trading = getOne(tradingId);
            Assert.state(buyerId != trading.getUserId(), "不能购买自己出售的积分");
            Assert.state(trading != null && trading.getAmount() >= amount, "购买数量超过售卖数量");
            Double payMoney = getRmbFactor();
            TradeOrder tradeOrder =
                    new TradeOrder(buyerId, amount, payMoney, trading.getPayInfo(), trading.getType(), tradingId);
            int id;
            do {
                id = NumberUtil.randomInt(9);
            } while (tradeOrderService.existsById(id));
            tradeOrder.setId(id);
            tradeOrderService.save(tradeOrder);
            trading.setAmount(trading.getAmount() - amount);
            trading.setTradingTime(new Date());
            save(trading);
        }
    }

    public Page<Trading> getPage(PageRequestMap pageRequestMap) {
        Criteria<Trading> conditions = getTradings(pageRequestMap, false).addOrderByAsc("time");
        return getPage(conditions);
    }

    private Criteria<Trading> getTradings(PageRequestMap pageRequestMap, boolean showall) {
        Criteria<Trading> conditions = createCriteria(pageRequestMap).addOrderByDesc("orders");
        Integer maxIndex = pageRequestMap.getIntValue("maxIndex");
        if (maxIndex != null) {
            conditions.limitMaxIndex(maxIndex);
        }
        conditions.addOrderByAsc("time");
        Criteria<Trading> criteria = conditions;
        if (!showall)
            criteria.andGt("amount", 0);
        String username = pageRequestMap.get("username");
        if (isNotBlank(username))
            criteria.andEqual("user.username", username);
        criteria.andEqual(pageRequestMap.asMap());
        return conditions;
    }

    public Page<Trading> getPageAdmin(PageRequestMap pageRequestMap) {
        Criteria<Trading> conditions = getTradings(pageRequestMap, true).addOrderByDesc("time");
        return getPage(conditions);
    }

    /*public void loadBalanceLog(List<Trading> tradings) {
        Set<Integer> logIds = tradings.stream().map(t -> t.getLog().getUserId()).collect(Collectors.toSet());
        List<BalanceLog> balanceLogs = balanceLogService.findAllById(logIds);
        Map<String, BalanceLog> balanceLogMap = balanceLogs.stream()
                .collect(Collectors.toMap(balanceLog -> balanceLog.getId() + "_" + balanceLog.getType(), p -> p));
        for (Trading trading : tradings) {
            trading.setLog(balanceLogMap.get(trading.getLog()));
        }
    }*/

    public void loadPayInfo(Collection<Trading> tradings) {
        Set<Integer> userIds = tradings.stream().map(t -> t.getUserId()).collect(Collectors.toSet());
        List<PayInfo> payInfos = payInfoService.findByUserInInAndTypeIn(userIds, PayInfoService.NOT_BANK_TYPE);
        Map<String, PayInfo> payInfoMap = payInfos.stream()
                .collect(Collectors.toMap(payInfo -> payInfo.getUserId() + "_" + payInfo.getType(), p -> p));
        for (Trading trading : tradings) {
            //"WEIXINPAY", "ALIPAY"
            trading.setAlipay(payInfoMap.get(trading.getUserId() + "_ALIPAY"));
            trading.setWeixinpay(payInfoMap.get(trading.getUserId() + "_WEIXINPAY"));
        }
    }

    public void loadUsername(Collection<Trading> tradings) {
        userService.loadUser(tradings, trading -> trading.getUserId(),
                (trading, user) -> trading.setUser(new User(user.getUsername())));
    }

    public Page<Trading> getUserPage(User user, PageRequestMap pageRequestMap) {
        Criteria<Trading> conditions = createCriteria(pageRequestMap);
        conditions.addOrderByAsc("time");
        Criteria<Trading> criteria = conditions;
        if (user != null) {
            criteria.andEqual("userId", user.getId());
        }
        return getPage(conditions);
    }

    /**
     * admin 撤销订单
     *
     * @param tradingId
     */
    @Transactional
    public void cancelTrandingAdmin(Integer tradingId) {
        synchronized (String.valueOf(true).intern()) {
            Trading trading = getOne(tradingId);
            //Assert.state( trading.getStatus() == -1, "订单已经被撤销！");
            Integer userId = trading.getUserId();
            Double amount =
                    trading.getAmount() * (1 - trading.getFees() / (trading.getLog().getAmount() + trading.getFees()));
            amount = NumberUtil.halfUp(amount, 2);
            Balance balance = balanceService.findOrCreateByUserIdAndType(userId, trading.getType());
            balanceService.incomeAndLog(balance, amount, Event.TRADE, "撤销,余额：" + trading.getAmount());
            trading.setAmount(0.0);
            trading.setStatus(Trading.STATUS_CANCEL);
            save(trading);
        }
    }
}
