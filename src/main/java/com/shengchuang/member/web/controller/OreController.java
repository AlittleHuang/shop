package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.domain.Ore;
import com.shengchuang.member.additional.service.OreService;
import com.shengchuang.member.additional.service.SettlementService;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.util.Assert;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

/**
 * 矿机挖矿
 */
@Controller
public class OreController extends AbstractController {

    @Autowired
    private OreService oreService;
    @Autowired
    private SettlementService settlementService;

    /**
     * 点击算力钻石
     *
     * @return
     */
    @PostMapping("/front/ore/mining")
    public View pickOreSettlement(Integer index) {
        Assert.notNull(index, "参数错误(int): ID");
        Ore ore = oreService.getByCurrDate(getSessionUser().getId());
        settlementService.pickOreSettlement(ore.getId(), index);
        return new JsonMap("今日挖矿成功、矿场继续开采中！<br>Mining continues to mine!");
    }

    @RequestMapping("/front/ore/info")
    public View info() {
        Ore ore = oreService.getByCurrDate(getSessionUser().getId());
        return new JsonMap().add("content", ore);
    }
}
