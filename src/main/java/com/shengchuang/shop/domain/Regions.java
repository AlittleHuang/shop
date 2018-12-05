package com.shengchuang.shop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 中国省市区
 */
@Data
@NoArgsConstructor
@Entity(name = "china_regions")
public class Regions {

    @Id
    Integer id;

    String name;

    Integer pid;

    /**
     * 0省,1市,2区
     */
    Integer type;

    //收货地址


}
