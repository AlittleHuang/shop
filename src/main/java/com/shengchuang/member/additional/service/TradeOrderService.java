package com.shengchuang.member.additional.service;


import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.BinaryUtil;
import com.shengchuang.common.util.StringUtil;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.base.AbstractService;
import com.shengchuang.member.trading.domain.TradeOrder;
import com.shengchuang.member.trading.domain.Trading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TradeOrderService extends AbstractService<TradeOrder, Integer> {

    private static final String KEY_OF_LAST_BUYER_INTEGRITY_CHECK_END_TIME = "last_buyer_integrity_check_end_time";
    private static final String KEY_OF_LAST_SELLER_INTEGRITY_CHECK_END_TIME = "last_seller_integrity_check_end_time";
    @Autowired
    private UserService userService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private TradingService tradingService;
    @Autowired
    private SettingsService settingsService;

    public Page<TradeOrder> getBuyPage(User buyer, PageRequestMap pageRequestMap) {
        Criteria<TradeOrder> conditions = requestToConditions(pageRequestMap).addOrderByDesc("time");
        conditions.and().andEqual("userId", buyer.getId());
        return getPage(conditions);
    }

    public Page<TradeOrder> getSellPage(User seller, PageRequestMap pageRequestMap) {
        Criteria<TradeOrder> conditions = requestToConditions(pageRequestMap).addOrderByDesc("time");
        if (conditions == null) {
            return emptyPage();
        }
        Criteria<TradeOrder> criteria = conditions.and().andEqual("trading.userId", seller.getId());
        return getPage(conditions);
    }

    public void loadBuyer(Collection<TradeOrder> tradeOrders) {
        userService.loadUser(tradeOrders,
                tradeOrder -> tradeOrder.getUserId(),
                (tradeOrder, user) -> tradeOrder.setBuyer(user));

    }

    /**
     * 确认交易
     */
    private void complete(int orderId, int sellerOrBuyer) {
        TradeOrder tradeOrder = getOne(orderId);
        Integer status = tradeOrder.getStatus();
        Assert.state(status >= TradeOrder.TRADING && status != TradeOrder.SUCCESS, "已完成或已取消的订单");
        Double amount = tradeOrder.getAmount();

        int statusFinal = BinaryUtil.setBit(status, sellerOrBuyer, true);
        logger.debug("status:" + status + "==>" + statusFinal);
        if (status == statusFinal) return;
        status = statusFinal;
        tradeOrder.setStatus(status);
        save(tradeOrder);
        if (status == TradeOrder.SUCCESS) {
            Balance balanceBuyer =
                    balanceService.findOrCreateByUserIdAndType(tradeOrder.getUserId(), tradeOrder.getType());
            balanceService.incomeAndLog(balanceBuyer, amount, Event.TRADE, "购买");
            tradeOrder.setStatus(TradeOrder.SUCCESS);
            tradeOrder.setUpdateTime(new Date());
            Trading trading = tradingService.getOne(tradeOrder.getTradingId());
            trading.setTradedTime(new Date());
        }
    }

    @Transactional
    public void completeBuyer(int id) {
        complete(id, TradeOrder.CONFIRM_BUYER);
    }

    @Transactional
    public void completeSeller(int id) {
        complete(id, TradeOrder.CONFIRM_SELLER);
    }

    @Transactional
    public void completeAdmin(int id) {
        complete(id, TradeOrder.CONFIRM_BUYER);
        complete(id, TradeOrder.CONFIRM_SELLER);
    }

    /**
     * 取消交易
     */
    @Transactional
    public void cancel(int orderId) {
        synchronized (String.valueOf(orderId).intern()) {
            TradeOrder tradeOrder = getOne(orderId);
            Integer status = tradeOrder.getStatus();
            Assert.state(status >= TradeOrder.TRADING && status != TradeOrder.SUCCESS, "已完成或已取消的订单");
            Double amount = tradeOrder.getAmount();
            Assert.state(amount > 0, "订单错误");
            Trading trading = tradingService.getOne(tradeOrder.getTradingId());
            trading.setAmount(trading.getAmount() + amount);
            //trading.setMargin(trading.getMargin() + amount * settingsService.get(TradingService.TRADE_EXCHANGE_RATE));
            //Assert.notNull(trading, "系统错误，请联系管理员");
            tradeOrder.setStatus(TradeOrder.CANCELED);
            tradeOrder.setUpdateTime(new Date());
            tradingService.save(trading);
            save(tradeOrder);
        }
    }

    public Page<TradeOrder> getPage(User user, PageRequestMap pageRequestMap) {
        Criteria<TradeOrder> conditions = requestToConditions(pageRequestMap).addOrderByDesc("time");
        Criteria<TradeOrder> criteria = conditions;
        if (user != null) {
            if (user.getId() != null) {
                criteria.orEqual("userId", user.getId()).orEqual("sellerId", user.getId());
            } else {
                conditions.setPageResultEmpty();
            }
        }
        String buyerUsername = pageRequestMap.get("buyer.username");
        if (StringUtil.notEmpty(buyerUsername)) {
            User buyer = userService.findByUsername(buyerUsername);
            if (buyer == null) {
                conditions.setPageResultEmpty();
            }
            criteria.andEqual("buyer.id", buyer.getId());
        }
        String sellerUsername = pageRequestMap.get("trading.username");
        if (StringUtil.notEmpty(sellerUsername)) {
            User seller = userService.findByUsername(sellerUsername);
            if (seller == null) {
                conditions.setPageResultEmpty();
            }
            criteria.andEqual("trading.userId", seller.getId());
        }
        return getPage(conditions);
    }

    private Criteria<TradeOrder> requestToConditions(PageRequestMap pageRequestMap) {
        Criteria<TradeOrder> conditions = createCriteria(pageRequestMap);
        Criteria<TradeOrder> criteria = conditions.and()
                .andEqual(pageRequestMap.asMap());
        String buyerUsername = pageRequestMap.get("buyerUsername");
        if (isNotBlank(buyerUsername)) {
            User buyer = userService.findByUsername(buyerUsername);
            if (buyer == null) {
                conditions.setPageResultEmpty();
            }
            criteria.andEqual("userId", buyer.getId());
        }
        String sellerUsername = pageRequestMap.get("sellerUsername");
        if (isNotBlank(sellerUsername)) {
            User seller = userService.findByUsername(sellerUsername);
            if (seller == null) {
                conditions.setPageResultEmpty();
            }
            criteria.andEqual("sellerId", seller.getId());
        }
        Date startTime = pageRequestMap.getDateValue("startTime");
        if (startTime != null) {
            criteria.andGe("time", startTime, Date.class);
        }
        Date endTime = pageRequestMap.getDateValue("endTime");
        if (endTime != null) {
            endTime = TimeUtil.addDay(endTime, 1);
            criteria.andLt("time", endTime, Date.class);
        }
        String[] statuses = pageRequestMap.getArray("statuses");
        if (statuses != null && statuses.length != 0) {
            criteria.andIn("status", Arrays.stream(statuses).map(s -> Integer.valueOf(s)).collect(Collectors.toList()));
        }
        return conditions;
    }

//    private void checkUserIntegrity(Map<Integer, User> map, List<TradeOrder> listBuyerOrder, Function<TradeOrder, Integer> getUserId) {
//        if (!listBuyerOrder.isEmpty()) {
//            for (TradeOrder tradeOrder : listBuyerOrder) {
//                User user = map.get(getUserId.apply(tradeOrder));
//
//                Integer integrity = user.getIntegrity();
//                integrity = integrity == null ? 5 : integrity;
//                if (integrity == 0) continue;
//                user.setIntegrity(integrity - 1);
//            }
//        }
//    }

}
