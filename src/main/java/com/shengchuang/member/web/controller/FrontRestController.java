package com.shengchuang.member.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.shengchuang.member.additional.PriceInfoService;
import com.shengchuang.member.additional.domain.Bonus;
import com.shengchuang.member.additional.domain.MiningMachine;
import com.shengchuang.member.additional.domain.Ore;
import com.shengchuang.member.additional.service.*;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.member.additional.service.setting.domain.Settings;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.*;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.service.TransferLogService;
import com.shengchuang.member.core.service.WithdrawService;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.*;

@RestController
@RequestMapping({"/front", "/app/front"})
public class FrontRestController extends AbstractController {

    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private BonusService bonusService;
    @Autowired
    private TransferLogService transferLogService;
    @Autowired
    private MiningMachineService miningMachineService;
    @Autowired
    private OreService oreService;
    @Autowired
    private PriceInfoService priceInfoService;

    /**
     * 充值
     */
    @PostMapping("/balance/recharge")
    public View doRecharge(Recharge recharge) {
        checkSecondPwd();
        Assert.notNull(recharge.getBalanceType(), "参数错误");
        Assert.state(recharge.getBalanceType() == 0, "参数错误");

        Double amount = recharge.getAmount();
        Assert.notNull(amount, "请输入充值金额");
        Integer userId = getSessionUser().getId();
        Date now = new Date();
        recharge.setUserId(userId);
        recharge.setCreateTime(now);
        recharge.setStatus(0);
        recharge.setInfo(Event.RECHARGE.getName());

        Map<String, Double> priceInfo = priceInfoService.getPriceInfo();
        Settings settings = settingsService.getSettings();
        String bankAccount = settings.getBankAccount();
        Double mmdPrice = settings.getMmdPrice();
        double amountCNY = amount * mmdPrice;
//        double amountETH = amount * mmdPrice / priceInfo.get("ETH");
        double amountETH = amount / settings.getPriceMmdOfTch();
        JSONObject map = new JSONObject();
        map.put("amountCNY", amountCNY);
        map.put("amountETH", amountETH);
        map.put("addr", bankAccount);

        recharge.setInfo(map.toJSONString());

        rechargeService.add(recharge);

        return new JsonMap("充值申请成功");
    }

    /**
     * 提现申请
     */
    @PostMapping("/balance/withdraw")
    public View doWithdraw(Withdraw withdraw) {
        String addr=withdraw.getAddr();
        Assert.notEmpty(addr,"请输入钱包地址");
        checkSecondPwd();//二级密码
        User user = refreshSessionUser();
        Assert.notNull(withdraw.getBalanceType(), "提现类型参数错误");
        //校验...
       //Sms sms = new SmsImpl(user.getPhone(), SmsImpl.SmsType.WITHDRAW).verify(request().getParameter("phonecode"));
        String day = toString(TimeUtil.getDayOfWeek());
        Settings settings = settingsService.getSettings();
        String days = settings.getWithdrawWeekDay();//可提现日期
        Assert.state(days.contains(day), TimeUtil.getDayNameOfWeek() + "不能提现");
        Double withdrawMin = settings.getWithdrawMin();
        Double amount = withdraw.getAmount();
        Assert.notNull(amount, "请输入提现金额");
        Assert.state(amount >= withdrawMin, "提现金额不能低于 " + withdrawMin);
        Double withdrawMax = settings.getWithdrawMax();
        Assert.state(amount <= withdrawMax, "提现金额不能大于 " + withdrawMax);
        Double factor = settings.getWithdrawFactor();
        Assert.state(amount % factor == 0, "提现金额必须是[ " + factor + " ]的整数倍");
        // 保存数据...
        Date now = new Date();
        withdraw.setFeerate(settings.getWithdrawFee());//手续费
        withdraw.setFee(amount);
        withdraw.setTramount(amount);
        withdraw.setUserId(user.getId());
        withdraw.setCreateTime(now);
        withdraw.setStatus(0);
        withdrawService.withdraw(withdraw);
        //sms.remove();
        return new JsonMap("申请成功");
    }


    /**
     * 奖金明细
     *
     * @return
     */
    @RequestMapping("/bonus/log")
    public View bonusLog() {
        Page<Bonus> page = bonusService.getPage(getSessionUser(), getPageRequestMap());
        bonusService.loadUsername(page.getContent());
        return new JsonVO(page);
    }

    /**
     * 奖金明细???
     *
     * @return
     */
    @RequestMapping("/bonus/sum/log")
    public View bonusLogOfsum() {
        int userId = getSessionUser().getId();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        // TODO /bonus/sum/log

        return new JsonVO(map);
    }

    /**
     * 算力钱包
     *
     * @return
     */
    @RequestMapping("/balance/rate")
    public View rate() {
        return new JsonVO("");
    }

    /**
     * 兑换明细
     */
    @RequestMapping("/transfer/log")
    public View transferLog() {
        User user = getSessionUser();
        Page<TransferLog> page = transferLogService.getPage(user, getPageRequestMap());
        return new JsonVO(page);
    }

    /**
     * 矿机明细
     */
    @RequestMapping("/miningMachine/log")
    public View miningMachineLog() {
        User user = getSessionUser();
        Page<MiningMachine> page = miningMachineService.getPage(user, getPageRequestMap());
        List<MiningMachine> content = page.getContent();
        Double machinePower = settingsService.getSettings().getMachinePower();
//        for (MiningMachine min : content) {
//            min.setPower(min.getPower() * machinePower);
//        }
        return new JsonVO(page);
    }


    /**
     * 获取个人当天算力详情
     */
    @RequestMapping("/ore/details")
    public View getOreDetails() {
        User user = getSessionUser();
        Ore ore = oreService.getByCurrDate(user.getId());
        return new JsonVO(ore);
    }

    /**
     * 消费累计统计   （在商城里消费）
     */
    @RequestMapping("/statistics/consumer")
    public View sum() {
        User user = getSessionUser();
        double sumAmount = balanceLogService.findSumAmount(user.getId(), Event.SHOPPING);
        return new JsonVO(sumAmount);
    }

    /**
     * 充值记录
     */
    @RequestMapping("/balance/recharge/log")
    public View rechargeLog() {
        PageRequestMap pageRequest = getPageRequestMap();
        Page<Recharge> page = rechargeService.listByUserId(getSessionUser().getId(), pageRequest);
        return new JsonVO(page);
    }

    /**
     * 提现记录
     */
    @RequestMapping("/balance/withdraw/log")
    public View withdrawLog() {
        Page<Withdraw> page = withdrawService.getPage(getSessionUser().getId(), getPageRequestMap());
        return new JsonVO(page);
    }

    /**
     * 推荐的会员
     */
    @RequestMapping("/user/children")
    public View childrenList() {
        User user = getSessionUser();
        Page<User> page = userService.getChrildrenPage(user, getPageRequestMap());
        //   userLevelService.loadUscLevel(page.getContent());
        return new JsonVO(page);
    }

    /**
     * 未激活会员列表
     */
    @RequestMapping("/user/regist-list")
    public View activeList() {
        User user = getSessionUser();
        Page<User> page = userService.getActivePage(user, getPageRequestMap());
        // Map<Integer, User> uMaps = userService.findAll().stream().collect(Collectors.toMap(u -> u.getId(), u -> u));
        userService.loadPusername(page.getContent());
        userService.loadUserInfo(page);
        return new JsonVO(page);
    }

}
