package com.shengchuang.shop.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.shengchuang.base.AbstractController;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.shop.domain.CartItem;
import com.shengchuang.shop.domain.Order;
import com.shengchuang.shop.service.CartService;
import com.shengchuang.shop.service.OrderService;
import com.shengchuang.shop.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.Map;

@Controller
public class OrderController extends AbstractController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ShippingAddressService shippingAddressService;

    @PostMapping("/api/buyer/order/add/from/product")
    public View add(Integer productItemId, Integer count,Integer addrId) {
        int buyerId = getLoginUserId();
        String addr = shippingAddressService.getOne(addrId).toJsonString();
        orderService.add(buyerId, productItemId, count, addr);
        return new JsonMap();
    }

    @PostMapping("/api/buyer/order/add/from/cart")
    public View add(Integer[] cartItemIds, Integer addrId) {
        int buyerId = getLoginUserId();
        String addr = shippingAddressService.getOne(addrId).toJsonString();
        List<CartItem> cartItems = cartService.findByIds(cartItemIds);
        cartService.toOrder(buyerId, addr, cartItems);
        return new JsonMap();
    }

    @RequestMapping("/api/seller/order/page")
    public View page(){
        Page<Order> page = orderService.criteria(getPageRequestMap()).getPage();
        return new JsonVO(page);
    }

}
