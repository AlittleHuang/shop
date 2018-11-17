package com.shengchuang.member.additional.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 购物日志
 */
@Data
@NoArgsConstructor
//@Accessors(chain = true)
//@Entity(name = "pay_log")
public class ShoppingLog {

    /**
     * 支付入日志类型
     */
    public static final int TYPE_OF_PAY = 0;

    /**
     * 发奖日志类型
     */
    public static final int TYPE_OF_BONUS = 1;

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 支付订单号
     */
    @Column(name = "out_order_id", unique = true, nullable = false)
    private String orderId;

    /**
     * 支付状态 0:未支付, 1:已支付, 2:已取消
     */
    private Integer status;

    /**
     * 金额
     */
    private Double amount;

    /**
     * 时间
     */
    private Date time;

    /**
     * 日志类型 0:支付,1:发奖
     */
    private Integer type;

    public ShoppingLog(Integer userId, String orderId, Integer status, Double amount, Integer type) {
        this.userId = userId;
        this.orderId = orderId;
        this.status = status;
        this.amount = amount;
        this.time = new Date();
        this.type = type;
    }
}
