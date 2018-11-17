package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.service.OrderFromAdService;
import com.shengchuang.member.additional.service.TradeOrderService;
import com.shengchuang.member.additional.service.TradingReportService;
import com.shengchuang.member.additional.service.TradingService;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.member.additional.service.setting.domain.Settings;
import com.shengchuang.common.exception.BusinessException;
import com.shengchuang.common.mvc.domain.PageFix;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.service.BalanceLogService;
import com.shengchuang.member.core.service.PayInfoService;
import com.shengchuang.member.trading.domain.OrderFromAd;
import com.shengchuang.member.trading.domain.TradeOrder;
import com.shengchuang.member.trading.domain.Trading;
import com.shengchuang.member.trading.domain.TradingReport;
import com.shengchuang.member.trading.repository.OrderFromAdRepository;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;

import java.time.LocalTime;
import java.util.stream.Collectors;

@Controller
public class TradingController extends AbstractController {

    @Autowired
    private TradingService tradingService;

    @Autowired
    private TradeOrderService tradeOrderService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private PayInfoService payInfoService;
    @Autowired
    private BalanceLogService balanceLogService;
    @Autowired
    private TradingReportService tradingReportService;

    @Autowired
    private OrderFromAdService orderFromAdService;
    @Autowired
    private OrderFromAdRepository orderFromAdDao;

    /**
     * 交易统计  每天
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/admin/trading/daily/list")
    public com.shengchuang.common.mvc.domain.Page<TradingReport> getDailylist() {
        return tradingReportService.getPage(getPageRequestMap());
    }

    /*  */

    /**
     * 售卖
     *
     * @param
     * @return
     *//*
    @PostMapping("/front/trading/add")
    public View add(Trading trading) {
        checkTime();
        Assert.notNull(trading.getType(), "缺少参数：type(int)");
        Assert.state(trading.getType() == Balance.TYPE_4, "type参数必须为4");
        checkSecondPwd();
        checkUserInfo(getSessionUser());
        checkEpAmount(trading.getAmount());
        User user = getSessionUser();
        trading.setUserId(user.getId());
        long timesToday = tradingService.createCriteria()
                .andEqual("userId", trading.getUserId())
                .andGe("time", TimeUtil.getStartTimeToday())
                .count();
        int timesPerDay = settingsService.getSettings().getEpTimesPerDay();
        Assert.state(timesPerDay > timesToday, "每天最多挂卖" + timesPerDay + "次");
        tradingService.add(trading);
        return new JsonMap("操作成功");
    }*/
    public void checkTime() {
        LocalTime now = LocalTime.now();
        LocalTime[] times;
        try {
            times = settingsService.getSettings().epTime();
        } catch (Exception e) {
            throw new BusinessException("休市中");
        }
        logger.debug(now.isAfter(times[0]));
        logger.debug(now.isBefore(times[1]));
        Assert.state(now.isAfter(times[0]) && now.isBefore(times[1]), "休市中");
    }

    private void checkUserInfo(User user) {
        /*Assert.notNull(user.getIdCard(), "请先完善个人信息");
        Assert.notNull(user.getActualName(), "请先完善个人信息");*/
        Assert.state(payInfoService.findUndeleteByUserId(user.getId()) > 0, "请先完善收款信息");
    }

    /**
     * 挂卖列表
     *
     * @return
     */
    @RequestMapping("/front/trading/list")
    public View getPage() {
        PageRequestMap pageRequestMap = getPageRequestMap();

        Page<Trading> page = tradingService.getPage(pageRequestMap);
        tradingService.loadPayInfo(page.getContent());
        tradingService.loadUsername(page.getContent());
        return new JsonVO(page);
    }

    @RequestMapping("/front/trading/user/list")
    public View getUserPage() {
        User user = getSessionUser();
        PageRequestMap pageRequestMap = getPageRequestMap();
        Page<Trading> page = tradingService.getUserPage(user, pageRequestMap);
        tradingService.loadUsername(page.getContent());
        return new JsonVO(page);
    }

    @RequestMapping("/admin/trading/list")
    public View getAllPage() {
        PageRequestMap pageRequestMap = getPageRequestMap();
        PageFix<Trading> page = (PageFix<Trading>) tradingService.getPageAdmin(pageRequestMap);
        tradingService.loadUsername(page.getContent());
        tradingService.loadPayInfo(page.getContent());
        //tradingService.loadBalanceLog(page.getContent());
        return new JsonVO(page);
    }

    @RequestMapping("/front/trading/list/sell")
    public View getSell() {
        PageRequestMap pageRequestMap = getPageRequestMap();
        Page<TradeOrder> page = tradeOrderService.getSellPage(getSessionUser(), pageRequestMap);
        //tradeOrderService.loadBuyer(page.getContent());
        tradingService.loadPayInfo(page.getContent().stream().map(o -> o.getTrading()).collect(Collectors.toList()));
        return new JsonVO(page);
    }

    /**
     * 购买记录
     *
     * @return
     */
    @RequestMapping("/front/trading/list/buy")
    public View getBuyList() {
        PageRequestMap pageRequestMap = getPageRequestMap();
        Page<TradeOrder> page = tradeOrderService.getBuyPage(getSessionUser(), pageRequestMap);
        //tradeOrderService.loadSellersUsername(page.getContent());
        tradingService.loadPayInfo(
                page.getContent().stream().map(tradeOrder -> tradeOrder.getTrading()).collect(Collectors.toList()));
        return new JsonVO(page);
    }

    /**
     * 购买
     *
     * @param tradingId
     * @param amount
     * @return
     */
    @RequestMapping("/front/trading/buy")
    public View buy(Integer tradingId, Double amount) {
        checkSecondPwd();
        checkTime();
        User user = getSessionUser();
        checkUserInfo(user);
        //Assert.state(user.getIntegrity() == null || user.getIntegrity() > 0, "信用不足");
        //Assert.state(amount%100==0,"购买的数量必须是100的整数倍");
        checkEpAmount(amount);
        tradingService.buy(user.getId(), tradingId, amount);
        return new JsonMap("购买成功");
    }

    /**
     * admin撤销挂卖订单
     */
    @RequestMapping("/admin/trading/cancel")
    public View cancelTrandingAdmin(Integer tradingId) {
        tradingService.cancelTrandingAdmin(tradingId);
        return new JsonMap("撤销成功！");
    }

    /**
     * 撤销挂卖订单
     */
    @RequestMapping("/front/trading/cancel")
    public View cancelTranding(Integer tradingId) {
        tradingService.cancelTrandingAdmin(tradingId);
        return new JsonMap("撤销成功！");
    }

    /**
     * 回购
     */
    @RequestMapping("admin/trading/repurchase")
    public View repurchase(Integer tradingId, Double amount) {
        //tradingService.repurchase(tradingId, amount);
        return new JsonMap("回购成功");
    }

    /**
     * 回购
     */
    @RequestMapping("/admin/trading/top")
    public View top(Integer tradingId) {
        Trading one = tradingService.getOne(tradingId);
        if (one != null) {
            long orders = System.currentTimeMillis() % (5 * TimeUtil.MILLIS_PER_DAY * 365);
            System.out.println(orders);
            one.setOrders(orders);
            tradingService.save(one);
        }
        return new JsonMap("置顶成功");
    }

    @RequestMapping("front/trading/seller/info")
    public View getSellerInfo(Integer orderId) {
        Assert.notNull(orderId, "缺少参数：orderId");
        TradeOrder order = tradeOrderService.getOne(orderId);
        Assert.notNull(order, "orderId不存在");
        Assert.state(getSessionUser().getId().equals(order.getUserId()), "orderId错误");
        User seller = userService.getOne(order.getPayInfo().getUserId());
        userService.loadUserInfo(seller);
        seller.setPassword(null);
        seller.setSecondpwd(null);
        return new JsonMap().add("content", seller);
    }

    @RequestMapping("front/trade/order/buy/complete")
    public View completeBuy(Integer orderId) {
        checkBuyerOrderId(orderId);
        tradeOrderService.completeBuyer(orderId);
        return new JsonMap().msg("操作成功");
    }

    @RequestMapping("/front/trade/order/buy/update/pay-proof")
    public View updatePayProof(Integer id, String payProof) {
        checkBuyerOrderId(id);
        TradeOrder one = tradeOrderService.getOne(id);
        one.setPayProof(payProof);
        tradeOrderService.save(one);

        return new JsonMap().msg("操作成功");
    }

    @RequestMapping("/front/trade/order/sell/complete")
    public View completeSell(Integer orderId) {
        checkSellerOrderId(orderId);
        tradeOrderService.completeSeller(orderId);
        return new JsonMap().msg("操作成功");
    }

    /**
     * 管理员完成订单
     *
     * @param orderId
     * @return
     */
    @RequestMapping("/admin/trade/order/complete")
    public View completeadmin(Integer orderId) {
        Assert.notNull(orderId, "缺少参数：orderId");
        TradeOrder order = tradeOrderService.getOne(orderId);
        Assert.notNull(order, "orderId不存在");
        tradeOrderService.completeAdmin(orderId);
        return new JsonMap().msg("操作成功");
    }

    @RequestMapping("/front/trade/order/cancel")
    public View cancel(Integer orderId) {
//        try {
//            checkSellerOrderId(orderId);
//        } catch (BusinessException e) {
        checkBuyerOrderId(orderId);
//        }
        tradeOrderService.cancel(orderId);
        return new JsonMap().msg("操作成功");
    }

    /**
     * 管理员取消订单
     *
     * @param orderId
     * @return
     */
    @RequestMapping("/admin/trade/order/cancel")
    public View adminCancel(Integer orderId) {
        try {
            Assert.notNull(orderId, "缺少参数：orderId");
            TradeOrder order = tradeOrderService.getOne(orderId);
            Assert.notNull(order, "orderId不存在");
        } catch (BusinessException e) {
            checkBuyerOrderId(orderId);
        }
        tradeOrderService.cancel(orderId);
        return new JsonMap().msg("操作成功");
    }

    private TradeOrder checkSellerOrderId(Integer orderId) {
        Assert.notNull(orderId, "缺少参数：orderId");
        TradeOrder order = tradeOrderService.getOne(orderId);
        Assert.notNull(order, "orderId不存在");
        Assert.state(getSessionUser().getId().equals(order.getTrading().getUserId()), "orderId错误");
        return order;
    }

    private TradeOrder checkBuyerOrderId(Integer orderId) {
        Assert.notNull(orderId, "缺少参数：orderId");
        TradeOrder order = tradeOrderService.getOne(orderId);
        Assert.notNull(order, "orderId不存在");
        Assert.state(getSessionUser().getId().equals(order.getUserId()), "orderId错误");
        return order;
    }

    @RequestMapping("/front/trade/order/list/success")
    public View successOrderList() {
        Page<TradeOrder> page = tradeOrderService.getPage(getSessionUser(), getPageRequestMap());
//        tradeOrderService.loadSellersUsername(page.getContent());
//        tradeOrderService.loadBuyer(page.getContent());
        return new JsonVO(page);
    }

    @RequestMapping("/admin/trade/order/list")
    public View orderList() {
        Page<OrderFromAd> page = orderFromAdService.getPage((User) null, getPageRequestMap());
//        tradeOrderService.loadSellersUsername(page.getContent());
//        tradeOrderService.loadBuyer(page.getContent());
        return new JsonVO(page);
    }

   /* @RequestMapping("/front/trade/sell/pay-info")
    public View lastPayInfo() {
        Trading trading = tradingService.createCriteria().andEqual("userId", getSessionUser().getId())
                .addOrderByDesc("time").limit(1).getOne();
        JsonMap jsonMap = new JsonMap();
        if (trading != null) {
            jsonMap.add("payInfo", trading.getPayInfo());
        } else {
            jsonMap.success(false);
        }
        jsonMap.add("star", refreshSessionUser().getIntegrity());
        jsonMap.add("sellRate", settingsService.getSettings().getEpFee());
        return jsonMap;
    }*/

    private void checkEpAmount(Double amount) {
        Assert.notNull(amount, "请输入交易数量");
        Settings settings = settingsService.getSettings();
        Assert.state(amount >= settings.getEpMin(), "交易数量至少 " + settings.getEpMin());
        Assert.state(amount <= settings.getEpMax(), "交易数量最多 " + settings.getEpMax());
        Assert.state(amount % settings.getEpFactor() == 0, "交易数量必须是 " + settings.getEpFactor() + "的整数倍");
    }

}
