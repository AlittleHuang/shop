package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.member.additional.service.setting.domain.Settings;
import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.BinaryUtil;
import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.PayInfoService;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.base.AbstractService;
import com.shengchuang.member.trading.domain.OrderFromAd;
import com.shengchuang.member.trading.domain.TradeOrder;
import com.shengchuang.member.trading.domain.TradingAd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

@Service
public class TradingAdService extends AbstractService<TradingAd, Integer> {

    private static final BalanceType TRADING_TYPE = BalanceType.MMD;
    @Autowired
    private OrderFromAdService orderFromAdService;
    @Autowired
    private UserService userService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private PayInfoService payInfoService;
    @Autowired
    private TradingService tradingService;

    private Double getRmbFactor() {
        return tradingService.getRmbFactor();
    }

    @Transactional
    public void add(TradingAd tradingAd, String action) {
        Integer userId = tradingAd.getUserId();
        if (tradingAd.getPayType() == 0) {
            Assert.state(!payInfoService.findByUserIdAndType(userId, "ALIPAY").isEmpty(), "未添加支付宝信息");
        } else if (tradingAd.getPayType() == 1) {
            Assert.state(!payInfoService.findByUserIdAndType(userId, "WEIXINPAY").isEmpty(), "未添加微信信息");
        } else if (tradingAd.getPayType() == 2) {
            Assert.notNull(payInfoService.findDefault(userId), "未添加银行卡信息");
        } else {
            Assert.notNull(null, "未添加付款信息");
        }
        tradingAd.setTime(new Date());
        tradingAd.setCount(0);
        tradingAd.setTraded(0.0);
        tradingAd.setTrading(0.0);
        tradingAd.setId(NumberUtil.randomInt(8));
        tradingAd.setStatus(0);
        tradingAd.setLimitedNum(0);
        Settings settings = settingsService.getSettings();


        Balance balance = balanceService.findOrCreateByUserIdAndType(userId, tradingAd.getBalanceType());
        if (action.equals("buy")) {//购买广告
            double buyFees = settings.getTradingAdFees() * tradingAd.getMax();
            double fees = tradingAd.getMax() * settings.getEpFee();
            tradingAd.setFees(buyFees);
            balanceService.expenditureAndLog(balance, buyFees + fees, Event.TRADE,
                    "发布购买广告，广告诚信金:" + buyFees + ",手续费：" + fees);
        } else if (action.equals("sell")) {//出售广告
            double sellFees = settings.getTradingAdSellFees() * tradingAd.getMax();
            tradingAd.setFees(sellFees);
            double fees = tradingAd.getMax() * settings.getEpSellFee();
            balanceService.expenditureAndLog(balance, tradingAd.getMax() + sellFees + fees, Event.TRADE,
                    "发布出售广告,广告诚信金:" + sellFees + ",手续费：" + fees);
        }
        save(tradingAd);
    }

    /**
     * 卖出
     *
     * @param userId 卖家id
     * @param id     广告ID
     * @param amount 交易金额
     */
    @Transactional
    public void order(int userId, Integer id, Double amount, String action) {
        Settings settings = settingsService.getSettings();
        TradingAd tradingAd = getOne(id);
        Assert.state(userId - tradingAd.getUserId() != 0, "这是您自己的广告哦");
        Double trading = tradingAd.getTrading();
        Double traded = tradingAd.getTraded();
        trading += amount;
        Assert.state(trading + traded <= tradingAd.getMax(), "交易数量超过剩余量");
        Assert.state(amount >= tradingAd.getMin(), "最低交易：" + tradingAd.getMin());
        tradingAd.setCount(tradingAd.getCount() + 1);
        tradingAd.setTrading(trading);
        save(tradingAd);
        User adUser = userService.getOne(tradingAd.getUserId());
        User user = userService.getOne(userId);
        OrderFromAd order;
        if (action.equals("sell")) {//购买广告
            order = new OrderFromAd(amount, userService.getOne(tradingAd.getUserId()), user, tradingAd);
            order.setUserId(userId);
            order.setPrice(tradingAd.getPrice());
            Balance balance = balanceService.findOrCreateByUserIdAndType(userId, tradingAd.getBalanceType());
            double sellFees = amount * settings.getEpSellFee();
            double sellDeposit = amount * settings.getTradingAdSellFees();
            order.setDeposit(sellDeposit);
            order.setFees(sellFees);

            orderFromAdService.save(order);
            String info = "出售给" + userService.getOne(tradingAd.getUserId()).getUsername() + ",手续费" + sellFees + ",诚信金" +
                    sellDeposit;
            balanceService.expenditureAndLog(balance, amount + sellFees + sellDeposit, Event.TRADE, info);
            try {
                new SmsImpl(tradingAd.getUser().getPhone(), "USC交易】您的节点编号："
                        + tradingAd.getUser().getUsername()
                        + ",C2C交易购买订单已生成，请及时查看卖家信息并及时转账，并在平台确认已打款，3小时内未打款将影响节点信誉及不退还诚信金。").send();
            } catch (Exception e) {
                logger.warn("交易通知短信发送失败", e);
            }
        } else if (action.equals("buy")) {//售出广告
            order = new OrderFromAd(amount, user, userService.getOne(tradingAd.getUserId()), tradingAd);
            order.setUserId(userId);
            order.setPrice(tradingAd.getPrice());
            Balance balance = balanceService.findOrCreateByUserIdAndType(userId, tradingAd.getBalanceType());
            double fees = amount * settings.getEpFee();
            double deposit = amount * settings.getTradingAdFees();
            order.setDeposit(deposit);
            order.setFees(fees);
            orderFromAdService.save(order);
            String info =
                    "从" + userService.getOne(tradingAd.getUserId()).getUsername() + "购买,手续费" + fees + ",诚信金" + deposit;
            balanceService.expenditureAndLog(balance, fees + deposit, Event.TRADE, info);
            try {
                new SmsImpl(tradingAd.getUser().getPhone(),
                        "【USC交易】您的节点编号："
                                + tradingAd.getUser().getUsername()
                                + ",C2C交易挂卖已售出，请及时登录查看详情，并查询是否已收到款，收款后请及时为买家确认，3小时内未确认将影响节点信誉及不退还诚信金。").send();
            } catch (Exception e) {
                logger.warn("交易通知短信发送失败", e);
            }
        }
    }

    @Transactional
    public void sellerComplete(int orderId) {
        complete(orderId, OrderFromAd.CONFIRM_SELLER, false, false);
    }

    @Transactional
    public void buyerComplete(int orderId) {
        complete(orderId, OrderFromAd.CONFIRM_BUYER, false, false);
    }

    @Transactional
    public void adminComplete(Integer id, boolean buyer, boolean seller) {
        complete(id, OrderFromAd.CONFIRM_ADMIN, buyer, seller);
    }

    @Transactional
    public void orderCancel(int orderId) {
        cancel(orderId, OrderFromAd.CONFIRM_SELLER);
    }

    @Transactional
    public void adderCancel(int orderId, int tradingAdType) {
        cancel(orderId, tradingAdType);
    }

    @Transactional
    public void adminCancel(int orderId, boolean buyer, boolean seller) {
        cancel(orderId, OrderFromAd.CONFIRM_ADMIN, buyer, seller);
    }

    //@Transactional
    private void cancel(int orderId, int who) {
        if (who == OrderFromAd.CONFIRM_SELLER) {//售出广告
            cancel(orderId, who, false, true);
        } else if (who == OrderFromAd.CONFIRM_BUYER) {//购买广告
            cancel(orderId, who, true, false);
        }
    }

    /**
     * 取消订单
     *
     * @param orderId   被取消的订单号
     * @param who       0买家,1卖家,2管理员
     * @param subBuyer  是否扣除卖家诚信金
     * @param subSeller 是否扣除卖家诚信金
     */
    // @Transactional
    private void cancel(int orderId, int who, boolean subBuyer, boolean subSeller) {
        OrderFromAd orderFromAd = orderFromAdService.getOne(orderId);
        TradingAd tradingAd = orderFromAd.getTradingAd();
        Integer status = orderFromAd.getStatus();
        Assert.state(status >= OrderFromAd.STATUS_T && status != OrderFromAd.STATUS_SUCCESS, "已完成或已取消的订单");
        orderFromAd.setStatus(-1 - who);
        Balance balanceSeller =
                balanceService.findOrCreateByUserIdAndType(orderFromAd.getSeller().getId(), tradingAd.getBalanceType());
        Balance balanceBuyer =
                balanceService.findOrCreateByUserIdAndType(orderFromAd.getBuyer().getId(), tradingAd.getBalanceType());
        Double amount = orderFromAd.getAmount();

        double returnAmount = orderFromAd.getFees() + orderFromAd.getDeposit();

        if (tradingAd.getType() == TradingAd.TYPE_BUY) {//购买广告
            if (subSeller) {
                returnAmount -= orderFromAd.getDeposit();
            }
            if (subBuyer) {
                tradingAd.setFees(0.0);
            }

            returnAmount += amount;
            balanceService.incomeAndLog(balanceSeller, returnAmount, Event.TRADE, "取消购买订单:" + orderId);
        } else if (tradingAd.getType() == TradingAd.TYPE_SELL) {//售出广告
            if (subSeller) {
                tradingAd.setFees(0.0);
            }
            if (subBuyer) {
                returnAmount -= orderFromAd.getDeposit();
            }

            balanceService.incomeAndLog(balanceBuyer, returnAmount, Event.TRADE, "取消购买订单:" + orderId);
        }
        tradingAd.setTrading(tradingAd.getTrading() - amount);
        orderFromAdService.save(orderFromAd);
        save(tradingAd);
    }

    /**
     * 确认交易
     */
    // @Transactional
    private void complete(int orderId, int role, boolean subBuyer, boolean subSeller) {
        Settings settings = settingsService.getSettings();
        OrderFromAd orderFromAd = orderFromAdService.getOne(orderId);
        Integer status = orderFromAd.getStatus();
        Assert.state(status >= OrderFromAd.STATUS_T && status < OrderFromAd.STATUS_S, "已完成或已取消的订单");
        if (role == OrderFromAd.CONFIRM_SELLER) {
            Assert.state(status != OrderFromAd.STATUS_T, "买家暂未打款，不能确认收款");
        }
        Double amount = orderFromAd.getAmount();
        int statusFinal = BinaryUtil.setBit(status, role, true);
        logger.debug("status:" + status + "==>" + statusFinal);
        if (status == statusFinal) return;
        status = statusFinal;
        orderFromAd.setStatus(status);
        orderFromAd.setUpdateTime(new Date());
        if (role == OrderFromAd.CONFIRM_BUYER) {
            orderFromAd.setBuyerSubTime(new Date());
            new SmsImpl(orderFromAd.getSeller().getPhone(), "【USC交易】您的节点编号："
                    + orderFromAd.getSeller().getUsername()
                    + ",C2C交易，买家已确认付款，请及时登录查看订单并确认收款，3小时内未确认将影响节点信誉及不退还诚信金。").send();
        } else if (role == OrderFromAd.CONFIRM_SELLER) {
            orderFromAd.setSellerSubTime(new Date());
            new SmsImpl(orderFromAd.getBuyer().getPhone(), "【USC交易】您的节点编号："
                    + orderFromAd.getBuyer().getUsername()
                    + ",C2C购买订单已完成，卖家已确认收款，请及时登录查看相应资产。").send();
        }
        if (status >= OrderFromAd.STATUS_S) {
            TradingAd tradingAd = orderFromAd.getTradingAd();

            Date time = orderFromAd.getTime();
            Date buyerSubTime = orderFromAd.getBuyerSubTime();
            Date sellerSubTime = orderFromAd.getSellerSubTime();
            long buyerCompleteDiff = buyerSubTime.getTime() - time.getTime();//打款毫秒时间差异
            long sellerCompleteDiff = sellerSubTime.getTime() - buyerSubTime.getTime();//收款毫秒时间差异
            Double buyerLimitedTime = settings.getSubBuyerComplete();//后台设置打款时间限制
            Double sellerLimitedTime = settings.getSubSellerComplete();//后台设置收款时间限制
            double buyerLimitedMs = buyerLimitedTime * 3600000;//转毫秒
            double sellerLimitedMs = sellerLimitedTime * 3600000;//转毫秒

            if (tradingAd.getType() == TradingAd.TYPE_BUY) {//购买广告

                if (buyerCompleteDiff > buyerLimitedMs) {
                    tradingAd.setLimitedNum(tradingAd.getLimitedNum() + 1);
                }

                Balance balanceSeller =
                        balanceService.findOrCreateByUserIdAndType(orderFromAd.getSeller().getId(),
                                tradingAd.getBalanceType());
                if (!subSeller && sellerCompleteDiff < sellerLimitedMs) {
                    balanceService.incomeAndLog(balanceSeller, orderFromAd.getDeposit(), Event.TRADE,
                            "交易成功,返还诚信金,订单号:" + orderFromAd.getId());
                    orderFromAd.setDeposit(0.0);
                }
                Balance balanceBuyer =
                        balanceService.findOrCreateByUserIdAndType(orderFromAd.getBuyer().getId(),
                                tradingAd.getBalanceType());
                if (subBuyer) {
                    tradingAd.setFees(0.0);
                }
                balanceService.incomeAndLog(balanceBuyer, amount, Event.TRADE,
                        "从" + orderFromAd.getSeller().getUsername() + "购买");

                orderFromAd.setStatus(TradeOrder.SUCCESS);
                orderFromAd.setUpdateTime(new Date());
                tradingAd.trad(amount);
                if (tradingAd.getMax() - tradingAd.getTraded() == 0) {
                    tradingAd.setStatus(1);
                    if (tradingAd.getFees() > 0 && tradingAd.getLimitedNum() == 0) {
                        balanceService.incomeAndLog(balanceBuyer, tradingAd.getFees(), Event.TRADE,
                                "交易成功,返还广告诚信金");
                        tradingAd.setFees(0.0);
                    }
                }
            } else if (tradingAd.getType() == TradingAd.TYPE_SELL) {//售出广告
                if (sellerCompleteDiff > sellerLimitedMs) {
                    tradingAd.setLimitedNum(tradingAd.getLimitedNum() + 1);
                }

                Balance balanceBuyer =
                        balanceService.findOrCreateByUserIdAndType(orderFromAd.getBuyer().getId(),
                                tradingAd.getBalanceType());
                if (!subBuyer && buyerCompleteDiff < buyerLimitedMs) {
                    balanceService.incomeAndLog(balanceBuyer, orderFromAd.getDeposit(), Event.TRADE,
                            "交易成功,返还诚信金,订单号:" + orderFromAd.getId());
                    orderFromAd.setDeposit(0.0);
                }

                Balance balanceSeller =
                        balanceService.findOrCreateByUserIdAndType(orderFromAd.getSeller().getId(),
                                tradingAd.getBalanceType());
                if (subBuyer) {
                    tradingAd.setFees(0.0);
                }
                balanceService.incomeAndLog(balanceBuyer, amount, Event.TRADE,
                        "从" + orderFromAd.getSeller().getUsername() + "购买");

                orderFromAd.setStatus(TradeOrder.SUCCESS);
                orderFromAd.setUpdateTime(new Date());
                tradingAd.trad(amount);
                if (tradingAd.getMax() - tradingAd.getTraded() == 0) {
                    tradingAd.setStatus(1);
                    if (tradingAd.getFees() > 0 && tradingAd.getLimitedNum() == 0) {
                        balanceService.incomeAndLog(balanceSeller, tradingAd.getFees(), Event.TRADE,
                                "交易成功,返还广告诚信金");
                        tradingAd.setFees(0.0);
                    }
                }
            }
            save(tradingAd);
        }
        orderFromAdService.save(orderFromAd);
    }

    public Page<TradingAd> getPage(User user, PageRequestMap pageRequestMap) {
        Criteria<TradingAd> criteria = toPageCriteria(pageRequestMap);
        Integer maxIndex = pageRequestMap.getIntValue("maxIndex");
        if (maxIndex != null) {
            criteria.limitMaxIndex(maxIndex);
        }
        if (user != null) {
            criteria.andEqual("userId", user.getId());
        }
        addTimeFilter(criteria, pageRequestMap, "time");//按时间范围筛选
        return criteria.addOrderByDesc("time").getFixPage();
    }

//    public void loadPayInfo(OrderFromAd orderFromAd) {
//        User seller = orderFromAd.getSeller();
//        Integer userId = seller.getId();
//        List<PayInfo> alipay = payInfoService.findByUserIdAndType(userId, "ALIPAY");
//        if (!alipay.isEmpty())
//            seller.setAliPay(alipay.get(0));
//        List<PayInfo> weixinpay = payInfoService.findByUserIdAndType(userId, "WEIXINPAY");
//        if (!weixinpay.isEmpty())
//            seller.setWeixinPay(weixinpay.get(0));
//        PayInfo payInfo = payInfoService.findDefault(userId);
//        seller.setBankCard(payInfo);
//    }

    @Transactional
    public void cancel(Integer id) {
        TradingAd one = getOne(id);
        Assert.state(one.getStatus() == 0, "已取消或已完成的广告");
        long count = orderFromAdService.createCriteria().andEqual("tradingAd.id", id)
                .andIn("status", Arrays.asList(OrderFromAd.STATUS_T, OrderFromAd.STATUS_S, OrderFromAd.STATUS_B))
                .count();
        Assert.state(count == 0, "订单正在交易,不能取消");
        Date time = one.getTime();
        Date now = new Date();
        long diff = now.getTime() - time.getTime();//毫秒时间差
        long settingTime = 24 * 3600000;//转毫秒
        Balance balance = balanceService.findOrCreateByUserIdAndType(one.getUserId(), one.getBalanceType());
        double amount = one.getMax() - one.getTraded();
        double fess = one.getFees();
        if (diff < settingTime) {//24小时内取消订单，不退还交易金
            if (one.getType() == TradingAd.TYPE_SELL) {//退还未完成
                balanceService.incomeAndLog(balance, amount, Event.TRADE, "取消广告，退回剩余未交易的金额");
            }
        } else {//24小时后取消订单，退还交易金
            if (one.getType() == TradingAd.TYPE_SELL) {//退还未完成
                balanceService.incomeAndLog(balance, amount + fess, Event.TRADE, "取消广告，退回剩余未交易的金额及广告诚信金：" + fess);
            } else {
                balanceService.incomeAndLog(balance, fess, Event.TRADE, "取消广告，退回广告诚信金：" + fess);
            }
        }

        one.setStatus(1);
        save(one);
    }

    /**
     * 统计总挂买条数
     */
    public Long getSumTradingAd() {
        Criteria<TradingAd> criteria = createCriteria();
        return criteria.count();
    }
}
