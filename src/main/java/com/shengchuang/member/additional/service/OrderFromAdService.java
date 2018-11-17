package com.shengchuang.member.additional.service;

import com.shengchuang.common.mvc.domain.Page;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.base.AbstractService;
import com.shengchuang.member.trading.domain.OrderFromAd;
import org.springframework.stereotype.Service;

@Service
public class OrderFromAdService extends AbstractService<OrderFromAd, Integer> {

    public Page<OrderFromAd> getPage(User user, PageRequestMap pageRequestMap) {
        Criteria<OrderFromAd> conditions = toPageConditions(pageRequestMap);
        Criteria<OrderFromAd> criteria = conditions;
        conditions.addOrderByDesc("time");
        String type = pageRequestMap.get("type");
        String sold = pageRequestMap.get("sold");
        if (user != null) {
            if (type != null && !"".equals(type) && type.equals("buy")) {
                criteria.andEqual("buyer.id", user.getId());
            } else if (type != null && !"".equals(type) && type.equals("mycs")) {
                criteria.andEqual("seller.id", user.getId());
            } else if (type != null && !"".equals(type) && type.equals("all")) {
                criteria.orEqual("buyer.id", user.getId());
                criteria.orEqual("seller.id", user.getId());
            }
        }
        if (sold != null && !"".equals(sold) && sold.equals("no")) {//未下架
            criteria.andNotEqual("status", OrderFromAd.STATUS_CANCEL);
        } else if (sold != null && !"".equals(sold) && sold.equals("yes")) {//已下架
            criteria.andEqual("status", OrderFromAd.STATUS_CANCEL);
        }
        String status = pageRequestMap.get("status");//狀態
        if (status != null && !"".equals(status)) {
            criteria.andEqual("status", status);
        }

        addTimeFilter(criteria, pageRequestMap, "time");//时间范围筛选

        return getPage(conditions);
    }

}
