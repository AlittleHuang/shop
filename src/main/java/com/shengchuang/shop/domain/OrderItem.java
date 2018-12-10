package com.shengchuang.shop.domain;

import com.shengchuang.common.util.Assert;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 订单
 */
@Data
@NoArgsConstructor
@Entity(name = "order_item")
public class OrderItem {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 购买数量
     */
    private Integer count;

    /**
     * 商品
     */
    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private ProductItem item;

    public OrderItem(ProductItem item, int count) {
        Assert.state(count > 0, "购买数量必须大于0");
        this.count = count;
        this.item = item;
    }
}
