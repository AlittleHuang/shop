package com.shengchuang.shop.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.shengchuang.common.util.JsonUtil;
import com.shengchuang.shop.service.RegionsService;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

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
    String fullAddress;

    @JSONField
    public String getFullAddress() {
        if (fullAddress == null) {
            fullAddress = RegionsService.toString(regionsId);
        }
        return fullAddress;
    }

    public String toJsonString() {
        Map<Object, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("receiver", receiver);
        map.put("address", getFullAddress() + address);
        return JsonUtil.encode(map);
    }

}
