package com.shengchuang.shop.web.controller;

import com.shengchuang.base.AbstractController;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.member.core.domain.BalanceLog;
import com.shengchuang.shop.domain.Product;
import com.shengchuang.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("shopTestController")
public class TestController extends AbstractController {

    @Autowired
    ProductService productService;

    @RequestMapping("/user/test")
    public Object test() {
        List<Product> list = productService.criteria(getPageRequestMap().setSort()).getList();
        return new JsonVO(list);
    }

}
