package com.shengchuang.shop.domain;

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

    public OrderItem(ProductItem item, Integer count) {
        this.count = count;
        this.item = item;
    }
}
