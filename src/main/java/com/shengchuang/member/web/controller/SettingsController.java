package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.service.TradingService;
import com.shengchuang.member.additional.service.setting.BonusSettingsService;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.member.additional.service.setting.domain.BonusSettings;
import com.shengchuang.member.additional.service.setting.domain.Settings;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.mvc.view.JsonView;
import com.shengchuang.common.util.BeanUtil;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import javax.servlet.ServletContext;
import java.util.HashMap;

//import com.shengchuang.member.additional.service.setting.SettingsService.BonusSettings;

@Controller
public class SettingsController extends AbstractController {

    @Autowired
    private SettingsService settingsService;
    @Autowired
    private BonusSettingsService bonusSettingsService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private TradingService tradingService;

    @RequestMapping("/admin/settings/update")
    public JsonMap updateSettings(Settings settings) {
        settings.epTime();
        settingsService.save(settings);
        servletContext.setAttribute("bonusSettings", settings);
        return new JsonMap("更新成功");
    }

    @RequestMapping("/admin/settings/dedicated/update")
    public JsonMap updateSettings(BonusSettings bonusSettings) {
        checkAdminSecondPwd();
        bonusSettingsService.save(bonusSettings);
        return new JsonMap("更新成功");
    }

    @GetMapping("/admin/settings/dedicated")
    public View getDedicatedSettings() {
        return new JsonVO(bonusSettingsService.getSettings());
    }

    @GetMapping("/admin/settings")
    public View getSettings() {
        return new JsonVO(settingsService.getSettings());
    }

    //充值银行卡信息
    @GetMapping("/front/settings/recharge")
    public JsonView rechargeSettings() {
        Settings settings = settingsService.getSettings();
        return new JsonMap()
                .add("bankAccount", settings.getBankAccount())
                .add("bankUserName", settings.getBankUserName())
                .add("bankType", settings.getBankType())
                .add("tips", settings.getTips());
    }

//    //挂卖手续费
//    @GetMapping("/front/bonusSettings/factor")
//    public JsonView factorSettings() {
//        BonusSettings bonusSettings = baseSettingsService.getSettings();
//        return new JsonMap()
//                .add("sellRate", bonusSettings.getEpFee());
//    }

    //充值银行卡信息
    @GetMapping("/front/settings/withdraw")
    public JsonView withdrawSettings() {
        Settings settings = settingsService.getSettings();
        return new JsonMap()
                .add("withdrawFactor", settings.getWithdrawFactor())
                .add("withdrawMin", settings.getWithdrawMin())
                .add("withdrawMax", settings.getWithdrawMax())
                .add("withdrawFee", settings.getWithdrawFee());
    }

    //交易钱包与人民币汇率
    @GetMapping({"/front/settings/rmbFactor", "/admin/settings/rmbFactor"})
    public JsonView rmbFactorSettings() {
        Settings settings = settingsService.getSettings();
        return new JsonMap()
                .add("rmbFactor", tradingService.getRmbFactor())
                .add("priceOfUsdt", settings.getPriceOfUsdt())
                ;
    }
    //手续费
    @RequestMapping("/front/settings/fee")
    public JsonView getFee(){
        Settings settings = settingsService.getSettings();
        return new JsonMap().add("fee",settings.getWithdrawFee());
    }

    //U资产价格
    @GetMapping("/front/settings/uzc/price")
    public JsonView uzcPriceSettings() {
        Settings settings = settingsService.getSettings();
        return new JsonMap()
                .add("uzcPrice", settings.getUzcPrice());
    }

    @GetMapping("/front/settings")
    public JsonView epSettings(String[] names) {
        if (names == null) return null;
        Settings settings = settingsService.getSettings();
        HashMap<String, Object> map = new HashMap<>();
        for (String name : names) {
            try {
                Object value = BeanUtil.getBeanProperties(settings, name);
                map.put(name, value);
            } catch (Exception e) {
                logger.warn("servlet uri: /front/bonusSettings", e);
            }
        }
        return new JsonVO(map);
    }


    @GetMapping("/front/bonus/settings")
    public JsonView settings(String[] names) {
        if (names == null) return null;
        BonusSettings settings = bonusSettingsService.getSettings();
        HashMap<String, Object> map = new HashMap<>();
        for (String name : names) {
            try {
                Object value = BeanUtil.getBeanProperties(settings, name);
                map.put(name, value);
            } catch (Exception e) {
                logger.warn("servlet uri: front/bonus/settings", e);
            }
        }
        return new JsonVO(map);
    }

}
