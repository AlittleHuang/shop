package com.shengchuang.shop.service;

import com.shengchuang.base.BaseService;
import com.shengchuang.common.util.Assert;
import com.shengchuang.shop.domain.Order;
import com.shengchuang.shop.domain.OrderItem;
import com.shengchuang.shop.domain.Product;
import com.shengchuang.shop.domain.ProductItem;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OrderService extends BaseService<Order, Integer> {


    public void add(Integer buyerId, Integer productItemId, Integer count, String addr) {

        Assert.state(count > 0, "");
        ProductItem productItem = commonDao.createCriteria(ProductItem.class).andEqual("id", productItemId)
                .andEqual("product.status", Product.STATUS_ONLINE).getOne();
        Assert.state(productItem != null, "商品不存在");
        assert productItem != null;
        Assert.state(productItem.getInventory() >= count, "库存不足");

        productItem.setInventory(productItem.getInventory() - count);
        OrderItem orderItem = commonDao.save(new OrderItem(productItem, count));
        Order order = new Order(buyerId, Collections.singletonList(orderItem), addr);
        commonDao.save(order);

    }
}
