package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.service.OrderFromAdService;
import com.shengchuang.member.additional.service.TradingAdService;
import com.shengchuang.member.additional.service.TradingService;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.member.additional.service.setting.domain.Settings;
import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.mvc.view.JsonView;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.StringUtil;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.service.PayInfoService;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.member.trading.domain.OrderFromAd;
import com.shengchuang.member.trading.domain.TradingAd;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@RestController
public class TradingAdController extends AbstractController {

    BiConsumer<TradingAd, User> setPhone = (tradingAd, user) -> {
        User u = new User();
        String phone = user.getPhone();
        if ("admin".equals(phone))
            phone = "18688888666";
        u.setPhone(StringUtil.hidePhone(phone));
        u.setImgsrc(user.getImgsrc());
        tradingAd.setUser(u);
    };
    @Autowired
    private TradingAdService tradingAdService;
    @Autowired
    private TradingService tradingService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private OrderFromAdService orderFromAdService;
    @Autowired
    private UserService userService;
    @Autowired
    private PayInfoService payInfoService;
    @Autowired
    private UserController userController;

    private Double getRmbFactor() {
        return settingsService.getSettings().getMmdPrice();
    }

    @PostMapping("/login/trading")
    public View login(String phone, String password) {
        Assert.notEmpty(phone, "请输入用户名或手机号码");
        Assert.notEmpty(password, "请输入密码");
        User login = userController.login(phone, password);
        Assert.notNull(login, "账号或密码错误");
        Integer freeze = login.getFreeze();
        Assert.state(freeze == null || freeze == User.FREEZE_JD,
                "您的账号已被冻结，请联系管理员解冻！");
        setSessionUser(login);
        JsonMap jsonMap = new JsonMap().msg("登录成功").add("sessionid", getSession().getId());
        return jsonMap;
    }

    @PostMapping("/front/trading/ad/add")
    public View addAd(TradingAd tradingAd, String action, String secondpwd) {
        checkSecondPwd(secondpwd);
        checkUserInfoByType(getSessionUser(), tradingAd.getPayType());
        Settings settings = settingsService.getSettings();
        String epWeekDay = settings.getEpWeekDay();
        Assert.state(epWeekDay.contains(String.valueOf(TimeUtil.getDayOfWeek())),
                TimeUtil.getDayNameOfWeek() + "休市中");
        LocalTime[] times = settings.epTime();
        LocalTime now = LocalTime.now();

        Assert.state(now.isAfter(times[0]) && now.isBefore(times[1]),
                "休市中<br/><font style='color:red;'>请于" + settings.getEpTime() + "进行交易</font>");
        Double min = tradingAd.getMin();
        Double price = tradingAd.getPrice();
        Double mmdPrice = settings.getMmdPrice();//子链价格
        Double parentPrice = settings.getParentPrice();//母链价格
        Double max = tradingAd.getMax();
        if (action.equals("buy")) {//购买广告
            double epFactor = settings.getEpFactor();
            double epMax = settings.getEpMax();
            double epMin = settings.getEpMin();
            Assert.notNull(price, "请输入购买价格");

            Assert.notNull(min, "请输入最小购买数量");
            Assert.notNull(tradingAd.getMax(), "请输入最大购买数量");
            Assert.state(max >= min, "最小数量不能大于最大数量");
            Assert.state(max % epFactor == 0 && min % epFactor == 0, "数量必须是" + epFactor + "的倍数");
            Assert.state(max <= epMax && min >= epMin, "数量必须在" + epMin + "和" + epMax + "之间");
            tradingAd.setType(TradingAd.TYPE_BUY);
        } else if (action.equals("sell")) {//出售广告
            double epSellFactor = settings.getEpSellFactor();
            double epSellMax = settings.getEpSellMax();
            double epSellMin = settings.getEpSellMin();
            Assert.notNull(price, "请输入出售价格");

            Assert.notNull(min, "请输入最小出售数量");
            Assert.notNull(tradingAd.getMax(), "请输入最大出售数量");
            Assert.state(max >= min, "最小数量不能大于最大数量");
            Assert.state(max % epSellFactor == 0 && min % epSellFactor == 0, "数量必须是" + epSellFactor + "的倍数");
            Assert.state(max <= epSellMax && min >= epSellMin, "数量必须在" + epSellMin + "和" + epSellMax + "之间");
            tradingAd.setType(TradingAd.TYPE_SELL);
        }
        Assert.state(tradingAd.getPayType() >= 0 && tradingAd.getPayType() < 3, "币种错误");
        tradingAd.setUserId(getSessionUser().getId());
        tradingAdService.add(tradingAd, action);
        return new JsonMap("发布成功");
    }

    @PostMapping("/front/trading/ad/trade")
    public View ordered(Integer id, Double amount, String action) {
        TradingAd ad = tradingAdService.getOne(id);
        checkUserInfoByType(getSessionUser(), ad.getPayType());
        Settings settings = settingsService.getSettings();
        String epWeekDay = settings.getEpWeekDay();
        Assert.state(epWeekDay.contains(String.valueOf(TimeUtil.getDayOfWeek())),
                TimeUtil.getDayNameOfWeek() + "休市中");
        LocalTime[] times = settings.epTime();
        LocalTime now = LocalTime.now();
        User sessionUser = getSessionUser();
        Integer userId = sessionUser.getId();
        Assert.state(now.isAfter(times[0]) && now.isBefore(times[1]),
                "休市中<br/><font style='color:red;'>请于" + settings.getEpTime() + "进行交易</font>");
        Assert.notNull(amount, "请输入金额");
        Assert.state(amount > 0, "金额错误");
        Assert.state(tradingAdService.existsById(id), "页面错误,请刷新重新购买");
        if (action.equals("buy")) {
            Double buyToAdMaxPerTimes = settings.getBuyToAdMaxPerTimes();
            double epFactor = settings.getEpFactor();
            Integer countBuyPerDay = settings.getBuyToAdTimesPerDay();
            Assert.state(amount % epFactor == 0, "数量必须是" + epFactor + "的倍数");
            long count = orderFromAdService.createCriteria().andEqual("userId", userId)
                    .andBetween("time", TimeUtil.getStartTimeToday(), TimeUtil.getOverTimeToday())
                    .andEqual("tradingAd.type", OrderFromAd.TYPE_SELL)
                    .andGe("status", OrderFromAd.STATUS_T)
                    .count();
            Assert.notNull(id, "页面错误,请刷新重新购买");
            Assert.state(amount <= buyToAdMaxPerTimes, "每次最多购买" + buyToAdMaxPerTimes);
            Assert.state(count < countBuyPerDay, "每天最多购买" + countBuyPerDay + "次");
        } else if (action.equals("sell")) {
            Double sellToAdMaxPerTimes = settings.getSellToAdMaxPerTimes();
            double epSellFactor = settings.getEpSellFactor();
            Integer countSellPerDay = settings.getSellToAdTimesPerDay();
            Assert.state(amount % epSellFactor == 0, "数量必须是" + epSellFactor + "的倍数");
            long count = orderFromAdService.createCriteria().andEqual("userId", userId)
                    .andBetween("time", TimeUtil.getStartTimeToday(), TimeUtil.getOverTimeToday())
                    .andEqual("tradingAd.type", OrderFromAd.TYPE_BUY)
                    .andGe("status", OrderFromAd.STATUS_T)
                    .count();
            Assert.notNull(id, "页面错误,请刷新重新出售");
            Assert.state(amount <= sellToAdMaxPerTimes, "每次最多出售" + sellToAdMaxPerTimes);
            Assert.state(count < countSellPerDay, "每天最多出售" + countSellPerDay + "次");
        }
        checkSecondPwd();
        tradingAdService.order(userId, id, amount, action);
        String phone = tradingAdService.getOne(id).getUser().getPhone();
        return new JsonMap("成功");
    }

    @RequestMapping("/front/trading/ad/all/list")
    public View adList(String from) {
        PageRequestMap pageRequestMap = getPageRequestMap();
        int epShowMax = 0;
        if (from.equals("buy")) {
            pageRequestMap.add("type", TradingAd.TYPE_BUY);
            pageRequestMap.add("size", settingsService.getSettings().getEpShowMax());
            epShowMax = settingsService.getSettings().getEpShowMax();
        } else if (from.equals("sell")) {
            pageRequestMap.add("size", settingsService.getSettings().getEpShowSellMax());
            pageRequestMap.add("type", TradingAd.TYPE_SELL);
            epShowMax = settingsService.getSettings().getEpShowSellMax();
        }
        pageRequestMap.add("page", 1);
        pageRequestMap.add("status", 0);

        Page<TradingAd> page = tradingAdService.getPage((User) null, pageRequestMap);
        userService.loadUser(page.getContent(), tradingAd -> tradingAd.getUserId(), setPhone);
        double epPrice = getRmbFactor();
        double parentPrice = settingsService.getSettings().getParentPrice();
        return new JsonMap(page).add("price", epPrice).add("parentPrice", parentPrice);
    }

    @RequestMapping("/admin/trading/ad/buy/list")
    public View listAll() {
        PageRequestMap pageRequestMap = getPageRequestMap();
        Page<TradingAd> page = tradingAdService.getPage((User) null, pageRequestMap);
        userService.loadUser(page.getContent(), tradingAd -> tradingAd.getUserId(), (trading, user) -> {
            trading.setUser(user);
        });
        double epPrice = getRmbFactor();
        return new JsonMap(page).add("price", epPrice);
    }

    @GetMapping({"/front/trading/ad/buy", "/admin/trading/ad/buy"})
    public View tradingAd(Integer id) {
        if (id == null) return new JsonMap().success(false);
        TradingAd one = tradingAdService.getOne(id);
        userService.loadUser(Arrays.asList(one), t -> t.getUserId(), setPhone);
        Double usdTocny = settingsService.getSettings().getUsdTocny();
        return new JsonMap().add(one).add("price", getRmbFactor()).add("usdTocny", usdTocny);
    }

    @GetMapping("/front/order/from/ad/ing")
    public View orderTrading(Integer tradingId) {
        PageRequestMap pageRequestMap = getPageRequestMap();
        pageRequestMap.add("size", 100);
        pageRequestMap.add("page", 1);
        Page<OrderFromAd> page = orderFromAdService.getPage(pageRequestMap, criteria -> {
            Integer userid = getSessionUser().getId();
            criteria.andIn("status", Arrays.asList(OrderFromAd.STATUS_T, OrderFromAd.STATUS_S, OrderFromAd.STATUS_B));
            if (tradingId != null) {
                criteria.andEqual("tradingAd.id", tradingId);
            } else {
                criteria.and().orEqual("buyer.id", userid).orEqual("seller.id", userid);
            }
        });
        return new JsonMap(page);
    }

    @GetMapping("/front/order/from/ad/ed")
    public View orderTraded(Integer tradingId) {
        PageRequestMap pageRequestMap = getPageRequestMap();
        pageRequestMap.add("size", 100);
        pageRequestMap.add("page", 1);
        Page<OrderFromAd> page = orderFromAdService.getPage(pageRequestMap, criteria -> {
            Integer userid = getSessionUser().getId();
            criteria.andNotIn("status",
                    Arrays.asList(OrderFromAd.STATUS_T, OrderFromAd.STATUS_S, OrderFromAd.STATUS_B));
            if (tradingId != null) {
                criteria.andEqual("tradingAd.id", tradingId);
            } else {
                criteria.and().orEqual("buyer.id", userid).orEqual("seller.id", userid);
            }
        });
        return new JsonMap(page);
    }

    /**
     * 买家确认
     *
     * @param id
     * @return
     */
    @PostMapping("/front/order/complete")
    public View complete(Integer id, String secondpwd) {
        checkSecondPwd(secondpwd);
        OrderFromAd one = orderFromAdService.getOne(id);
        if (getSessionUser().getId() - one.getSeller().getId() == 0)
            tradingAdService.sellerComplete(id);
        else if (getSessionUser().getId() - one.getBuyer().getId() == 0)
            tradingAdService.buyerComplete(id);
        return new JsonMap("操作成功");
    }

    @PostMapping("/front/order/cancel")
    public View cancelOrder(Integer id, String secondpwd) {
        checkSecondPwd(secondpwd);
        OrderFromAd one = orderFromAdService.getOne(id);
        Assert.state(getSessionUser().getId() - one.getBuyer().getId() == 0
                        || getSessionUser().getId() - one.getSeller().getId() == 0
                , "取消失败，请刷新重试");
        TradingAd tradingAd = one.getTradingAd();
        Assert.state(getSessionUser().getId() - tradingAd.getUserId() == 0, "不是你的广告不能取消");
        //one.getTradingAd().setFees(0.0);
        tradingAdService.adderCancel(id, tradingAd.getType());
        return new JsonMap("操作成功");
    }

    /**
     * 交易完成
     *
     * @param id
     * @param buyer  是否扣除卖家诚信金
     * @param seller 是否扣除卖家诚信金
     * @return
     */
    @PostMapping("/admin/order/ad/complete")
    public View completeAdderAdmin(Integer id, Boolean buyer, Boolean seller) {
        //OrderFromAd one = orderFromAdService.getOne(id);
        //Assert.state(getSessionUser().getId() - one.getTradingAd().getUserId() == 0, "!@#$");
        buyer = buyer != null && buyer;
        seller = seller != null && seller;
        tradingAdService.adminComplete(id, buyer, seller);
        return new JsonMap("操作成功");
    }

    @PostMapping("/front/order/from/ad/cancel")
    public View cancelAdder(Integer id) {
        OrderFromAd one = orderFromAdService.getOne(id);
        Assert.state(getSessionUser().getId() - one.getTradingAd().getUserId() == 0, "取消失败，请刷新重试");
        TradingAd tradingAd = one.getTradingAd();
        Assert.state(getSessionUser().getId() - tradingAd.getUserId() == 0, "不是你的广告不能取消");
        tradingAdService.adderCancel(id, tradingAd.getType());
        return new JsonMap("操作成功");
    }

    /**
     * 后台 取消交易
     *
     * @param id     订单id
     * @param buyer  是否扣除卖家诚信金
     * @param seller 是否扣除卖家诚信金
     * @return
     */
    @PostMapping("/admin/order/from/ad/cancel")
    public View cancelAdderAdmin(Integer id, Boolean buyer, Boolean seller) {
        buyer = buyer != null && buyer;
        seller = seller != null && seller;
        tradingAdService.adminCancel(id, buyer, seller);
        return new JsonMap("操作成功");
    }

    @PostMapping("/front/tradingAd/cancel")
    public View cancelTradingAd(Integer id) {
        //OrderFromAd one = orderFromAdService.getOne(id);
        //Assert.state(getSessionUser().getId() - one.getTradingAd().getUserId() == 0, "!@#$");
        TradingAd one = tradingAdService.getOne(id);
        Integer userId = getSessionUser().getId();
        boolean equals = one.getUserId().equals(userId);
        Assert.state(equals, "取消失败，请刷新重试");
        tradingAdService.cancel(id);
        return new JsonMap("操作成功");
    }


    /**
     * 我的购买广告
     *
     * @return
     */
    @RequestMapping("/front/trading/ad/list")
    public View myAdList() {
        PageRequestMap pageRequestMap = getPageRequestMap();
        Page<TradingAd> page = tradingAdService.getPage(getSessionUser(), pageRequestMap);
        double epPrice = getRmbFactor();
        userService.loadUser(page.getContent(), tradingAd -> tradingAd.getUserId(), setPhone);
        return new JsonMap(page).add("price", epPrice);
    }

    /**
     * 广告详情
     *
     * @return
     */
    @RequestMapping("/front/trading/ad/details")
    public View adDetails(Integer id) {
        Assert.notNull(id, "请回广告列表重新进入");
        TradingAd tradingAd = tradingAdService.getOne(id);
        User user = userService.getOne(tradingAd.getUserId());
        double epPrice = getRmbFactor();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("tradingAd", tradingAd);
        map.put("user", user);
        map.put("price", epPrice);
        return new JsonMap(map);
    }

    /**
     * 下架
     *
     * @param id
     */
    @RequestMapping("/front/trading/ad/soldOut")
    public void soldOut(Integer id) {
        Assert.isTrue(true, "暂不支持手动下架");
        TradingAd tradingAd = tradingAdService.getOne(id);
        tradingAd.setStatus(1);
        tradingAdService.save(tradingAd);
    }

    @GetMapping("/front/ad/order")
    public JsonView getAdOrder(Integer id) {
        OrderFromAd one = orderFromAdService.getOne(id);
        Integer payType = tradingAdService.getOne(one.getTradingAd().getId()).getPayType();
        Integer balanceType = one.getTradingAd().getBalanceType();
//        tradingAdService.loadPayInfo(one);
        Double usdTocny = settingsService.getSettings().getUsdTocny();
        return new JsonMap().add(one)
                .add("loginId", getSessionUser().getId())
                .add("balanceType", balanceType)
                .add("usdTocny", usdTocny).add("payType", payType);
    }

    @PostMapping("/front/ad/order/image/update")
    public JsonView updateOrderSrc(Integer id, String src) {
        OrderFromAd order = orderFromAdService.getOne(id);
        Assert.state(order.getBuyer().getId() - getSessionUser().getId() == 0, "!@#$");
        order.setImgsrc(src);
        orderFromAdService.save(order);
        return new JsonMap("上传成功");
    }


    private void checkUserInfo(User user) {
        /*System.out.println("用户身份证:" + user.getIdCard());
        Assert.notNull(user.getIdCard(), "请先完善个人身份证号");
        System.out.println("真实姓名:" + user.getActualName());
        Assert.notNull(user.getActualName(), "请先完善个人真实姓名");*/
        Assert.state(payInfoService.findUndeleteByUserId(user.getId()) > 0, "请先完善收款信息");
    }

    private void checkUserInfoByType(User user, Integer payType) {
        /*Assert.notNull(user.getIdCard(), "请先完善个人身份证号");
        Assert.notNull(user.getActualName(), "请先完善个人真实姓名");*/
        Assert.state(payInfoService.findUndeleteByUserIdAndType(user.getId(), payType) > 0, "请先完善收款信息");
    }

    /**
     * 统计总挂买条数
     *
     * @return
     */
    @RequestMapping({"/front/trading/count", "/admin/trading/count"})
    public View userCount() {
        Long tradings = tradingAdService.getSumTradingAd();
        return new JsonVO(tradings);
    }

}
