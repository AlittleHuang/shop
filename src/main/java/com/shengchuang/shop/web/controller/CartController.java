package com.shengchuang.shop.web.controller;

import com.shengchuang.base.AbstractController;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.util.Assert;
import com.shengchuang.shop.domain.CartItem;
import com.shengchuang.shop.domain.Product;
import com.shengchuang.shop.domain.ProductItem;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.List;

@RestController
public class CartController extends AbstractController {

    private Criteria<ProductItem> getProductItemCriteria() {
        return commonDao.createCriteria(ProductItem.class);
    }

    private Criteria<CartItem> getCartItemCriteria() {
        return commonDao.createCriteria(CartItem.class);
    }

    /**
     * 添加到购物车
     *
     * @param id    {@link com.shengchuang.shop.domain.OrderItem#id OrderItem.id}
     * @param count 添加数量
     * @return
     */
    @PostMapping("/buyer/cart/add")
    public View addToCart(Integer id, Integer count) {
        Assert.notNull(id, "id错误");
        Assert.state(count != null && count > 0, "count error");
        boolean exists = getProductItemCriteria()
                .andEqual("id", id)
                .andEqual("product.status", Product.STATUS_ONLINE)
                .exists();
        Assert.state(exists, "商品不存在");

        int userId = 1;//TODO login user id

        CartItem item = getCartItemCriteria()
                .andEqual("userId", userId)
                .andEqual("productItemId", id)
                .getOne();

        if (item == null) {
            item = new CartItem(userId, id);
        }

        item.setCount(item.getCount() + count);
        commonDao.save(item);

        return new JsonMap();
    }


    /**
     * 购物车列表
     *
     * @return
     */
    @RequestMapping("/buyer/cart/list")
    public View list() {
        Integer userId = 1;//TODO login user id
        List<CartItem> list = getCartItemCriteria().andEqual("userId", userId).getList();
        return new JsonMap().add("list", list);
    }

    /**
     * 修改购物车购买数量
     *
     * @param cartId {@link CartItem#id} CartItem的id
     * @param count  修改后的数量
     * @return
     */
    @RequestMapping("/buyer/cart/item/count/update")
    public View updateCount(Integer cartId, Integer count) {
        Assert.state(count != null && count > 0, "count error");

        Integer userId = 1;// TODO login user id

        CartItem cartItem = commonDao.createCriteria(CartItem.class)
                .andEqual("userId", userId)
                .andEqual("id", cartId).getOne();

        if (cartItem != null) {
            cartItem.setCount(count);
            commonDao.save(cartItem);
        }
        return new JsonMap();
    }

}
