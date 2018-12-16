package com.shengchuang.shop.domain;

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
     * 新建（待审核）
     */
    public static final int STATUS_NEW = 0;
    /**
     * 上架
     */
    public static final int STATUS_ONLINE = 1;
    /**
     * 下架
     */
    public static final int STATUS_OFFLINE = 2;
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
     * 卖家
     */
    @ManyToOne
    @JoinColumn(nullable = false,
            foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Store store;

    /**
     * 状态
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 商品分类
     */
    @ManyToOne
    @JoinColumn(name = "categories_id",
            foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
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
    @JoinColumn(name = "product_id",foreignKey = @ForeignKey(name = "none"))
    private List<ProductItem> items;

}
