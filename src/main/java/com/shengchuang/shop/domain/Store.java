package com.shengchuang.shop.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * 店铺
 */
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "store")
public class Store {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 关联用户ID
     */
    @Column(unique = true)//一个用户只能有一个店铺
    private Integer userId;

    /**
     * 店铺名称
     */
    @Column(unique = true)
    private String name;


}
