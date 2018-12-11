package com.shengchuang.shop.service;

import com.shengchuang.base.BaseService;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.shop.domain.ShippingAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShippingAddressService extends BaseService<ShippingAddress, Integer> {

    @Transactional
    public ShippingAddress checkAndSave(ShippingAddress address) {

        if (address.getId() != null) {
            Criteria<ShippingAddress> criteria = criteria()
                    .andEqual("userId", address.getUserId());
            ShippingAddress addressDb = criteria
                    .andEqual("id", address.getId())
                    .getOne();
            Assert.notNull(addressDb, "id error");
        } else {
            address.setSortNum(-System.currentTimeMillis());
        }

        return saveSelective(address);

    }

}
