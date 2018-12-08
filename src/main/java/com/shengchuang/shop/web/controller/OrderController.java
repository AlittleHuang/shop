package com.shengchuang.shop.web.controller;

import com.shengchuang.base.AbstractController;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;

@Controller
public class OrderController extends AbstractController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/buyer/order/add")
    public View add(Integer productItemId, Integer count,Integer addrId) {
        int buyerId = 1;//TODO 购买用户Id
        String addr = "地址";
        orderService.add(buyerId, productItemId, count, addr);
        return new JsonMap();
    }

}
