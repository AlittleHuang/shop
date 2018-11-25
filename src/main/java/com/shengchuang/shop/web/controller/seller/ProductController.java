package com.shengchuang.shop.web.controller.seller;

import com.shengchuang.base.AbstractController;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.shop.domain.Product;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

@RestController
public class ProductController extends AbstractController {

    @PostMapping("/seller/product/add")
    public View addProduct(Product product) {




        return new JsonMap();
    }

}
