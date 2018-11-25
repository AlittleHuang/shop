package com.shengchuang.shop.web.controller;

import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.util.Assert;
import com.shengchuang.shop.domain.Categories;
import com.shengchuang.shop.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

@Controller
public class CategoriesController {

    @Autowired
    CategoriesService categoriesService;
    private static Categories ROOT;

    @RequestMapping("/seller/categories/save")
    public View save(Integer rootId, Integer pid, String name) {
        checkRoot();

        Assert.notEmpty(name, "请输入名称");
        Integer upId = ROOT.getId();
        if (pid != null && pid != -1) {
            upId = pid;
        } else if (rootId != -1) {
            upId = rootId;
        }
        Categories categories = new Categories(name);
        if (upId != null) {
            Categories one = categoriesService.getOne(upId);
            categories.setPid(one.getId());
        }

        try {
            categoriesService.save(categories);
        } catch (javax.persistence.PersistenceException e) {
            return new JsonMap().failedMsg("已存在");
        }
        return new JsonMap();
    }

    private void checkRoot() {
        if (ROOT == null) {
            synchronized (CategoriesController.class) {
                if (ROOT == null) {
                    ROOT = categoriesService.criteria().andIsNull("pid")
                            .addOrderByAsc("id").limit(1).getOne();
                    if (ROOT == null) {
                        ROOT = categoriesService.save(new Categories(""));
                    }
                }
            }
        }
    }

    @RequestMapping("/seller/categories/list")
    public View list() {
        checkRoot();
        return new JsonMap().add("list", categoriesService.criteria().orIsNotNull("pid").getList())
                .add("rootId", ROOT.getId());
    }
}
