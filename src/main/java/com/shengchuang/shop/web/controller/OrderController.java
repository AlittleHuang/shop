package com.shengchuang.shop.web.controller;

import com.shengchuang.base.AbstractController;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.shop.domain.CartItem;
import com.shengchuang.shop.service.CartService;
import com.shengchuang.shop.service.OrderService;
import com.shengchuang.shop.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;

import java.util.List;

@Controller
public class OrderController extends AbstractController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ShippingAddressService shippingAddressService;

    @PostMapping("/buyer/order/add/from/product")
    public View add(Integer productItemId, Integer count,Integer addrId) {
        int buyerId = getLoginUserId();
        String addr = shippingAddressService.getOne(addrId).toJsonString();
        orderService.add(buyerId, productItemId, count, addr);
        return new JsonMap();
    }

    @PostMapping("/buyer/order/add/from/cart")
    public View add(Integer[] cartItemIds, Integer addrId) {
        int buyerId = getLoginUserId();
        String addr = shippingAddressService.getOne(addrId).toJsonString();
        List<CartItem> cartItems = cartService.findByIds(cartItemIds);
        cartService.toOrder(buyerId, addr, cartItems);
        return new JsonMap();
    }

}
