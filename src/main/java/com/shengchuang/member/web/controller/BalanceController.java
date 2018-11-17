package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.service.TradingService;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.member.additional.service.setting.domain.Settings;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.mvc.view.JsonView;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.BalanceLog;
import com.shengchuang.member.core.domain.TransferSetting;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.TransferSettingsService;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

@Controller
public class BalanceController extends AbstractController {

    @Autowired
    private BalanceService balanceService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private TransferSettingsService transferSettingsService;

    @Autowired
    private TradingService tradingService;

    @RequestMapping("admin/balance/list")
    public View list() {
        Page<Balance> page = balanceService.getPage(getPageRequestMap());
        balanceService.loadUsers(page.getContent());
        // balanceService.loadSP(page.getContent().stream().map(balance ->
        // balance.getUser()).collect(Collectors.toList()));
        return new JsonVO(page);
    }

    /**
     * 获取币种数值
     *
     * @return
     */
    @RequestMapping("/front/balance/amount")
    public View balanceAmount() {
        Integer type = intParameter("type");
        Assert.notNull(type, "缺少参数(int): type");
        Balance balance = balanceService.findOrCreateByUserIdAndType(getSessionUser().getId(), type);
        return new JsonVO(balance.getAmount());
    }

    /**
     * 实时价格
     *
     * @return
     */
    @RequestMapping("/front/balance/rmbFactor")
    public View rmbFactor() {
        Settings settings = settingsService.getSettings();
        double rmbFactor = tradingService.getRmbFactor();
        return new JsonVO(rmbFactor);
    }

    /**
     * 提现手续费
     *
     * @return
     */
    @RequestMapping("/front/balance/withdrawFee")
    public View withdrawFee() {
        Settings settings = settingsService.getSettings();
        double withdrawFee = settings.getWithdrawFee();
        return new JsonVO(withdrawFee);
    }

    /**
     * 所有币种信息
     *
     * @return
     */
    @RequestMapping("/front/balance/all")
    public View balances() {
        return new JsonVO(balanceService.getBalances(getSessionUser().getId()));
    }

    /**
     * 所有币种信息
     *
     * @return
     */
    @RequestMapping("/front/balance/user")
    public View userBalances(int id) {
        return new JsonVO(balanceService.getBalances(id));
    }

    /**
     * 币种信息
     *
     * @return
     */
    @RequestMapping("/front/balance")
    public View balances(Integer type) {
        User user = getSessionUser();
        return new JsonVO(balanceService.findOrCreateByUserIdAndType(user.getId(), type));
    }

    /**
     * 币种信息
     *
     * @return
     */
    @RequestMapping("/admin/balance")
    public View balances(Integer userId, Integer type) {
        return new JsonVO(balanceService.findOrCreateByUserIdAndType(userId, type));
    }

    /**
     * 清零
     *
     * @param userId
     * @param type
     * @return
     */
    @RequestMapping("admin/balance/clean")
    public JsonView clean(Integer userId, Integer type) {
        balanceService.clean(userId, BalanceType.of(type));
        return new JsonMap("操作成功");
    }

    @RequestMapping({"/admin/balance/total", "/front/balance/total"})
    public JsonView total(Integer userId, Integer type) {
        return new JsonMap("操作成功").add("total", new double[]{totalByType(0)});
    }

    private double totalByType(int type) {
        Object o = balanceService.createCriteria().andEqual("type", type).addSelectSum("amount").getOneObject();
        return (double) (o == null ? 0.0 : o);
    }

    /**
     * 币种手续费
     *
     * @return
     */
    @RequestMapping("/front/balance/convertRate")
    public View convertRate(Integer typeIn, Integer typeOut, Integer transferType) {
        TransferSetting settings = transferSettingsService.getSettings(typeIn, typeOut, transferType);
        return new JsonVO(settings);
    }

    @GetMapping({"/admin/balance/types", "/front/balance/types"})
    public View balanceType() {
        return new JsonMap().addEnums("content", BalanceType.displays());
    }

    @GetMapping({"/admin/events/list"})
    public View invents() {
        return new JsonMap().add("content", BalanceLog.OPERATION_NAME)
                .add("defaultEvent", Event.RECHARGE_BY_ADMIN.getIndex());
    }

    @GetMapping("/admin/balance/sum")
    public View sum(){
        return new JsonMap().add("content",balanceService.sumAmountOfBalance(getParametersMap().asMap()));
    }
}
