package com.shengchuang.shop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * 支付记录
 */
@Data
@NoArgsConstructor
@Entity(name = "pay_log")
public class PayLog {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 支付订单号
     */
    @Column(name = "out_order_id")
    private String outOrderId;

    /**
     * 支付状态 0 - 发起, 1 - 支付成功, 2 - 支付失败, 3 - 取消
     */
    private Integer status;

    /**
     * 支付方式
     */
    private Integer payType;

    @OneToMany
    @JoinColumn(name = "pay_id")
    List<Order> orders;

}
