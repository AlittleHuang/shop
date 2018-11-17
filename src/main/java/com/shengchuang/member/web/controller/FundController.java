package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.PriceInfoService;
import com.shengchuang.member.additional.domain.Fund;
import com.shengchuang.member.additional.service.FundService;
import com.shengchuang.member.additional.service.TradingService;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.*;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.HashMap;
import java.util.Map;

import static com.shengchuang.member.additional.PriceInfoService.*;

@RestController
public class FundController extends AbstractController {

    @Autowired
    private FundService fundService;
    @Autowired
    private TradingService tradingService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    PriceInfoService priceInfoService;

    /**
     * 添加记录
     */
    @PostMapping("/front/add/fund")
    public View doRecharge(Fund fund) {
        checkSecondPwd();
        Assert.notNull(fund.getPhone(), "请输入手机号！");
        Assert.notNull(fund.getEmail(), "请输入Email！");
        Assert.notNull(fund.getFundType(), "请选择资产名称！");
        Assert.notNull(fund.getAmount(), "请转出数量！");
        if (marketList.contains(fund.getFundType())) {
//            Assert.notNull(fund.getPrice(), "请输入转出时价！");
            Double price = priceInfoService.getPriceInfo().get(fund.getFundType());
            Double uzcPrice = settingsService.getSettings().getMmdPrice();
            fund.setPrice(price / uzcPrice);
//            fund.setAmount(fund.getAmount() / fund.getPrice());

        }
        if (fund.getType() == 1)
            fund.setPrice(tradingService.getRmbFactor());
        Assert.notNull(fund.getTransferTimeStr(), "请选择转账时间！");
        Assert.notNull(fund.getProof(), "请上传转账凭证！");

        User user = getSessionUser();
        fund.setTransferTime(TimeUtil.parseDateTime(fund.getTransferTimeStr()));
        fund.setUserId(user.getId());

        fundService.add(fund);
        return new JsonMap("操作成功！");
    }

    @GetMapping("/front/vprice/info")
    public View getPriceInfo(String[] names) {
        Map<String, Double> priceInfo = priceInfoService.getPriceInfo();
        JsonMap jsonMap = new JsonMap();
        if (names == null || names.length == 0) {
            jsonMap.putAll(priceInfo);
            return jsonMap;
        }
        HashMap<String, Double> map = new HashMap<>();
        for (String name : names) {
            map.put(name, priceInfo.get(name));
        }
        jsonMap.putAll(map);
        return jsonMap;
    }

//    @RequestMapping("/front/price/info")
//    public String allPriceInfo() {
//        if (info == null || System.currentTimeMillis() > updateTime + 5 * TimeUtil.MILLIS_PER_MINUTE) {
//            info = priceInfo();
//            updateTime = System.currentTimeMillis();
//        }
//        return info;
//    }


    /**
     * 同意提现
     *
     * @param id
     * @return
     */
    @RequestMapping("/admin/fund/agree")
    public View withdrawAgree(Integer id) {
        fundService.agree(id);
        return new JsonMap("操作成功");
    }

    /**
     * 拒绝提现
     *
     * @param id
     * @return
     */
    @RequestMapping("/admin/fund/disagree")
    public View withdrawDisagree(Integer id) {
        fundService.disagree(id);
        return new JsonMap("操作成功");
    }

    /**
     * 记录 admin
     *
     * @return
     */
    @RequestMapping("/admin/fund/log")
    public View adminWithdrawLog() {
        Page<Fund> page = fundService.getPage(getPageRequestMap());
        return new JsonVO(page);
    }

    /**
     * 记录 front
     */
    @RequestMapping("/front/fund/log")
    public View frontWithdrawLog() {
        Page<Fund> page = fundService.getPage(getSessionUser().getId(), getPageRequestMap());
        return new JsonVO(page);
    }


}
