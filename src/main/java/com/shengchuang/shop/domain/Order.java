package com.shengchuang.shop.domain;

import com.shengchuang.member.core.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Data
@NoArgsConstructor
@Entity(name = "orders")
public class Order {

    /**
     * 新建状态(待付款)
     */
    public static final int STATUS_NEW = 0;
    /**
     * 已支付，未发货
     */
    public static final int STATUS_PAID = 1;

    /**
     * 已发货
     */
    public static final int STATUS_SHIP = 2;
    /**
     * 确认收货
     */
    public static final int STATUS_RECEIPT = 3;
    /**
     * 取消
     */
    public static final int STATUS_OFF = 4;
    /**
     * 删除
     */
    public static final int STATUS_DELETE = -1;

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 关联用户ID
     */
    @Column(name = "buyer_id")
    private Integer buyerId;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems;

    /**
     * 订单状态
     */
    private Integer status;

    private Date createTime;

    /**
     * 收货地址
     */
    private String address;

    public Order(Integer buyerId, List<OrderItem> orderItems, String address) {
        this.buyerId = buyerId;
        this.orderItems = orderItems;
        this.address = address;
        this.createTime = new Date();
        this.status = STATUS_NEW;
    }
}
