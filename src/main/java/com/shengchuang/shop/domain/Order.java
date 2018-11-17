package com.shengchuang.shop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

/**
 *
 */
@Data
@NoArgsConstructor
@Entity(name = "orders")
public class Order {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems;

    /**
     * 订单状态
     */
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "pay_id")
    private PayLog payLog;

}
