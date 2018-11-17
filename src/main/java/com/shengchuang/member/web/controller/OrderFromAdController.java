package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.service.OrderFromAdService;
import com.shengchuang.member.additional.service.TradingService;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.trading.domain.OrderFromAd;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderFromAdController extends AbstractController {

    @Autowired
    private OrderFromAdService orderFromAdService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private TradingService tradingService;

    private Double getRmbFactor() {
        return tradingService.getRmbFactor();
    }

    /**
     * 订单列表
     *
     * @return
     */
    @GetMapping("/front/order/ad/list")
    public View orderAdList() {
        PageRequestMap pageRequestMap = getPageRequestMap();
        Page<OrderFromAd> page = orderFromAdService.getPage(getSessionUser(), pageRequestMap);
        double epPrice = getRmbFactor();
        return new JsonMap(page).add("price", epPrice);
    }

    /**
     * 詳情
     *
     * @param id
     * @return
     */
    @RequestMapping("/front/order/ad/details")
    public View orderAdDetails(Integer id) {
        OrderFromAd orderFromAd = orderFromAdService.getOne(id);
        double epPrice = getRmbFactor();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("orderFromAd", orderFromAd);
        map.put("price", epPrice);
        return new JsonMap(map);
    }

    /**
     * 下架
     */
    @RequestMapping("/front/order/ad/soldOut")
    public View soldOut(Integer id) {
        Assert.state(true, "暂不支持手动下架");
        OrderFromAd orderFromAd = orderFromAdService.getOne(id);
        orderFromAd.setStatus(OrderFromAd.STATUS_CANCEL);
        orderFromAdService.save(orderFromAd);
        return new JsonMap("操作成功");
    }
}
