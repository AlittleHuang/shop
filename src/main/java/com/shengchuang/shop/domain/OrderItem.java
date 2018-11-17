package com.shengchuang.shop.domain;

import com.shengchuang.member.core.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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
     * 关联用户ID
     */
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date time;

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

}
