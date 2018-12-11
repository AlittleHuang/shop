package com.shengchuang.shop.domain;

import com.shengchuang.shop.service.RegionsService;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * 收货地址
 */
@Data
@NoArgsConstructor
@Entity(name = "shipping_address")
public class ShippingAddress {

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

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 排序字段
     */
    private Long sortNum;

    @Transient
    List<RegionsService.Node> nodes;

    public ShippingAddress initRegionsNode() {
        nodes = RegionsService.getProvinceCitArea(regionsId);
        return this;
    }

}
