package com.shengchuang.shop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 购物车列表
 */
@Data
@NoArgsConstructor
@Entity(name = "cart_item")
public class CartItem {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 关联用户ID
     */
    private Integer userId;

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
    @JoinColumn(name = "item_id", updatable = false, insertable = false,
            foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private ProductItem item;

    @Column(name = "item_id")
    private Integer productItemId;

    public CartItem(Integer userId, Integer productItemId) {
        this.userId = userId;
        this.count = 0;
        this.productItemId = productItemId;
        status = 0;
        time = new Date();
    }
}
