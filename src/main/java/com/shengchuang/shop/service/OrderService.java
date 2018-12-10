package com.shengchuang.shop.service;

import com.shengchuang.base.BaseService;
import com.shengchuang.common.util.Assert;
import com.shengchuang.shop.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class OrderService extends BaseService<Order, Integer> {

    public Order add(Integer buyerId, Integer productItemId, Integer count, String addr) {

        Assert.state(count > 0, "");
        ProductItem productItem = commonDao.createCriteria(ProductItem.class).andEqual("id", productItemId)
                .andEqual("product.status", Product.STATUS_ONLINE).getOne();
        Assert.state(productItem != null, "商品不存在");
        assert productItem != null;
        Assert.state(productItem.getInventory() >= count, "库存不足");

        productItem.setInventory(productItem.getInventory() - count);
        OrderItem orderItem = commonDao.save(new OrderItem(productItem, count));
        Order order = new Order(buyerId, Collections.singletonList(orderItem), addr);
        return commonDao.save(order);

    }

    @Transactional
    public Order add(Integer buyerId, String addr, List<OrderItem> items) {
        List<OrderItem> orderItems = commonDao.saveAll(items, OrderItem.class);
        Order order = new Order(buyerId, orderItems, addr);
        return save(order);
    }
}
