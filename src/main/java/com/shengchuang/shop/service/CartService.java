package com.shengchuang.shop.service;

import com.shengchuang.base.BaseService;
import com.shengchuang.shop.domain.CartItem;
import com.shengchuang.shop.domain.Order;
import com.shengchuang.shop.domain.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService extends BaseService<CartItem, Integer> {

    @Autowired
    private OrderService orderService;

    @Transactional
    public Order toOrder(int buyerId, String addr, List<CartItem> cartItems) {
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> new OrderItem(cartItem.getItem(), cartItem.getCount()))
                .collect(Collectors.toList());
        deleteAll(cartItems);
        return orderService.add(buyerId, addr, orderItems);
    }
}
