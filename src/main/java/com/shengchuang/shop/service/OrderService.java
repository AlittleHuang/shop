package com.shengchuang.shop.service;

import com.shengchuang.base.BaseService;
import com.shengchuang.common.util.Assert;
import com.shengchuang.shop.domain.Order;
import com.shengchuang.shop.domain.OrderItem;
import com.shengchuang.shop.domain.Product;
import com.shengchuang.shop.domain.ProductItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
        Order order = new Order(buyerId, productItem.getProduct().getStore().getId(), Collections.singletonList(orderItem), addr);
        return commonDao.save(order);

    }

    @Transactional
    public List<Order> add(Integer buyerId, String addr, List<OrderItem> items) {

        List<OrderItem> orderItems = commonDao.saveAll(items, OrderItem.class);

        Map<Integer, List<OrderItem>> map = new HashMap<>();
        for (OrderItem item : orderItems) {
            Integer storeId = item.getItem().getProduct().getStore().getId();
            List<OrderItem> list = map.computeIfAbsent(storeId, ArrayList::new);
            list.add(item);
        }

        List<Order> list = new ArrayList<>();
        for (Map.Entry<Integer, List<OrderItem>> e : map.entrySet()) {
            list.add(new Order(buyerId, e.getKey(), e.getValue(), addr));
        }

        return saveAll(list);

    }
}
