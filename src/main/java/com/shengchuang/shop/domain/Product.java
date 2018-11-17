package com.shengchuang.shop.domain;

import com.shengchuang.member.core.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * 商品
 */
@Data
@NoArgsConstructor
@Entity(name = "product")
public class Product {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 卖家
     */
    @ManyToOne
    @JoinColumn(name = "seller_id" ,nullable = false)
    private User seller;

    /**
     * 状态
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 商品分类
     */
    @ManyToOne
    @JoinColumn(name = "categories_id")
    private Categories categories;

    /**
     * 商品名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 封面图片
     */
    @Column(nullable = false)
    private String coverImage;

    /**
     * 运费
     */
    @Column(nullable = false)
    private Double freight;

    /**
     * 商品详情(html格式)
     */
    @Lob
    @Column(nullable = false)
    private String details;

    /**
     * 规格
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private List<ProductItem> items;

}
