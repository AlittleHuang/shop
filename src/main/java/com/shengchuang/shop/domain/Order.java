package com.shengchuang.shop.domain;

import com.alibaba.fastjson.annotation.JSONField;
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
     * 用户ID
     */
    @Column(name = "buyer_id", nullable = false)
    private Integer buyerId;

    @ManyToOne
    @JoinColumn(name = "buyer_id", updatable = false, insertable = false,
            foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT)
    )
    private User buyer;

    /**
     * 店铺ID
     */
    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @ManyToOne
    @JoinColumn(name = "store_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT)
    )
    private Store store;

    @OneToMany
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "none"))
    private List<OrderItem> orderItems;

    /**
     * 订单状态
     */
    private Integer status;

    private Date createTime;

    /**
     * 订单金额,不含运费
     */
    @Column(nullable = false)
    private Double amount;

    /**
     * 订单运费
     */
    @Column(nullable = false)
    private Double freight;

    /**
     * 收货地址
     */
    private String address;

    public Order(int buyerId, int storeId, List<OrderItem> orderItems, String address) {
        this.buyerId = buyerId;
        this.storeId = storeId;
        this.orderItems = orderItems;
        this.address = address;
        this.createTime = new Date();
        amount = 0d;
        freight = 0d;
        for (OrderItem item : orderItems) {
            ProductItem productItem = item.getItem();
            amount += item.getCount() * productItem.getPrice();
            freight = Math.max(productItem.getProduct().getFreight(), freight);
        }

        this.status = STATUS_NEW;
    }

    @JSONField
    public double getTotalAmount() {
        return amount + freight;
    }


}
