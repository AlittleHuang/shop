package com.shengchuang.shop.web.controller;

import com.shengchuang.base.AbstractController;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.StringUtil;
import com.shengchuang.shop.domain.ShippingAddress;
import com.shengchuang.shop.service.RegionsService;
import com.shengchuang.shop.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.List;

@RestController
public class ShippingAddressController extends AbstractController {

    @Autowired
    private ShippingAddressService shippingAddressService;
    @Autowired
    private RegionsService regionsService;


    @RequestMapping("/api/byuer/shipping-address/save")
    public View add(ShippingAddress address) {
        Assert.notEmpty(address.getReceiver(), "请输入收货人");
        Assert.state(StringUtil.isPhoneNumber(address.getPhone()), "请输入正确的手机号码");
        Assert.state(regionsService.hasArea(address.getRegionsId()), "请选择所在地区");
        Assert.notEmpty(address.getAddress(), "请输入详细地址");
        address.setUserId(getLoginUserId());
        ShippingAddress saved = shippingAddressService.checkAndSave(address);
        return new JsonMap().add("id", saved.getId());
    }


    @RequestMapping("/api/byuer/shipping-address/list")
    public View list(ShippingAddress address) {
        List<ShippingAddress> list = shippingAddressService.criteria().andEqual(address).getList();
        return new JsonMap().add("list", list);
    }

    @RequestMapping("/api/byuer/shipping-address/delete")
    public View list(Integer id) {
        ShippingAddress address = shippingAddressService.criteria()
                .andEqual("userId", getLoginUserId())
                .andEqual("id", id)
                .getOne();
        shippingAddressService.delete(address);
        return new JsonMap();
    }

    @RequestMapping("/api/byuer/shipping-address/{id}")
    public View getOne(@PathVariable Integer id) {
        ShippingAddress data = shippingAddressService.criteria()
                .andEqual("userId", getLoginUserId())
                .andEqual("id", id)
                .getOne();
        Assert.notNull(data, "");
        return new JsonMap().add("data", data).add("regions", RegionsService.getProvinceCitArea(data.getRegionsId()))
                .add("fullAddress", RegionsService.toString(data.getRegionsId()) + data.getAddress());
    }

    @RequestMapping("/api/byuer/shipping-address/")
    public View getDefalut() {
        ShippingAddress data = shippingAddressService.criteria()
                .andEqual("userId", getLoginUserId())
                .limit(1)
                .getOne();

        if (data == null) return new JsonMap().success(false);
        return new JsonMap().add("data", data)
                .add("fullAddress", RegionsService.toString(data.getRegionsId()) + data.getAddress());
    }

}
