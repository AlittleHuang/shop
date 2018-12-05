package com.shengchuang.shop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity(name = "shopping_address")
public class ShoppingAddress {

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
     * 区域Id
     */
    private Integer regionsId;

    /**
     * 地址
     */
    private String address;

}
