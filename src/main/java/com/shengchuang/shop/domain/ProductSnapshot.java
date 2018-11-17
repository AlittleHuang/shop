package com.shengchuang.shop.domain;

import com.shengchuang.common.util.JsonUtil;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 快照
 */
@NoArgsConstructor
@Entity(name = "product_snapshot")
public class ProductSnapshot {

    /**
     * id
     */
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productId;

    @Lob
    private String content;

    public Product product() {
        return JsonUtil.decode(content, Product.class);
    }

    public ProductSnapshot(Product product) {
        this.productId = product.getId();
        this.content = JsonUtil.encode(product);
    }
}
