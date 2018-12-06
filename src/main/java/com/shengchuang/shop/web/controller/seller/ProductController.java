package com.shengchuang.shop.web.controller.seller;

import com.shengchuang.base.AbstractController;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.shop.domain.Product;
import com.shengchuang.shop.domain.ProductItem;
import com.shengchuang.shop.web.model.EasyUiGrid;
import com.shengchuang.shop.web.service.ProductService;
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

    @PostMapping("/seller/product/add")
    public View addProduct(Product product) {
        List<ProductItem> items = product.getItems();
        product.setSeller(userService.getOne(1));
        product.setStatus(0);
        product.setItems(null);
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


    @RequestMapping("/seller/product/list")
    public View pageList(){
        Page<Product> page = productService.criteria().getPage(getPageRequestMap());
        if("easyui".equals(request().getParameter("formatter"))){
            EasyUiGrid<Product> grid = new EasyUiGrid<>(page.getContent(), page.getTotalElements());
            return new JsonVO(grid);
        }
        return new JsonMap(page);
    }

    @RequestMapping("/app/product/{id}")
    public View getOne(@PathVariable("id") Integer id){
        Product one = productService.getOne(id);
        return new JsonMap().add("data",one);
    }

    @RequestMapping("/app/product/item/{id}")
    public View getItem(@PathVariable("id") Integer id) {
        ProductItem one = commonDao.getOne(ProductItem.class, id);
        return new JsonMap().add("data", one);
    }

}
