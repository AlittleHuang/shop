package com.shengchuang.shop.web.controller;

import com.shengchuang.base.AbstractController;
import com.shengchuang.common.mvc.repository.CommonDao;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.shop.domain.Product;
import com.shengchuang.shop.domain.ProductItem;
import com.shengchuang.shop.service.StoreService;
import com.shengchuang.shop.web.model.EasyUiGrid;
import com.shengchuang.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.List;

@RestController
public class ProductController extends AbstractController {
    @Autowired
    ProductService productService;
    @Autowired
    private StoreService storeService;

    @PostMapping("/api/seller/product/add")
    public View addProduct(Product product) {
        List<ProductItem> items = product.getItems();
        product.setStore(storeService.getOne(1));
        product.setStatus(0);
        product.setItems(null);
        product.setStore(storeService.getOne(getLoginStoreId()));
        Product save = productService.save(product);
        for (ProductItem item : items) {
            item.setProduct(save);
            if (item.getDescription() == null) {
                item.setDescription("{}");
            }
        }
        commonDao.saveAll(items, ProductItem.class);
        return new JsonMap();
    }


    @RequestMapping("/api/seller/product/list")
    public View pageList(){
        Page<Product> page = productService.criteria().getPage(getPageRequestMap());
        if("easyui".equals(request().getParameter("formatter"))){
            EasyUiGrid<Product> grid = new EasyUiGrid<>(page.getContent(), page.getTotalElements());
            return new JsonVO(grid);
        }
        return new JsonMap(page);
    }

    @RequestMapping("/api/public/product/{id}")//公共API
    public View getOne(@PathVariable("id") Integer id){
        Product one = productService.getOne(id);
        return new JsonMap().add("data",one);
    }

    @RequestMapping("/api/public/product/item/{id}")
    public View getItem(@PathVariable("id") Integer id) {
        ProductItem one = commonDao.getOne(ProductItem.class, id);
        return new JsonMap().add("data", one);
    }

}
