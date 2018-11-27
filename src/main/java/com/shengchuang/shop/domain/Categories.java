package com.shengchuang.shop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

/**
 * 商品分类
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = {"pid", "name"}))
public class Categories {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    /**
//     * 父级分类
//     */
//    @ManyToOne
//    @JoinColumn(name = "pid")
//    private Categories parant;

    private Integer pid;

    /**
     * 名称
     */
    private String name;

    @Transient
    private List<Categories> children;

    public Categories(Integer pid, String name) {
        this.pid = pid;
        this.name = name;
    }
}
