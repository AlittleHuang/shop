package com.shengchuang.shop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 规格型号
 */
@Data
@NoArgsConstructor
@Entity(name = "product_items")
public class ProductItem {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 规格, Json格式保存, 如: {"颜色":"黑","尺码":"XL"}
     */
    private String description;

    /**
     * 库存
     */
    private Integer inventory;

    /**
     * 商品
     */
    @ManyToOne
    @JoinColumn(name = "product_id",foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Product product;

    /**
     * 单价
     */
    private Double price;

    /**
     *
     */
    private Double marketPrice;


}
