package com.shengchuang.shop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 商品分类
 */
@Data
@NoArgsConstructor
@Entity(name = "categories")
public class Categories {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 父级分类
     */
    @ManyToOne
    @JoinColumn(name = "pid")
    private Categories parant;

    /**
     * 名称
     */
    private String name;

    /**
     * 级别
     */
    private Integer level;

    /**
     * 允许添加商品
     */
    @Column(name = "allow_products")
    private Boolean allowProducts;

}
