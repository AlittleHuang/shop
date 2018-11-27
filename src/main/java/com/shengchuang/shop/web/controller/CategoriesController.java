package com.shengchuang.shop.web.controller;

import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.Assert;
import com.shengchuang.shop.domain.Categories;
import com.shengchuang.shop.service.CategoriesService;
import com.shengchuang.shop.web.model.EasyuiTreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CategoriesController {

    @Autowired
    CategoriesService categoriesService;
    private static Categories ROOT;

    @RequestMapping("/seller/categories/save")
    public View save(Integer pid, String name) {
        checkRoot();

        Assert.notNull(pid, "请选择上级分类");
        Assert.notEmpty(name, "请输入名称");

        Categories one = categoriesService.getOne(pid);
        Assert.notNull(one, "请选择上级分类");

        Categories categories = new Categories(pid, name);



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
                        ROOT = categoriesService.save(new Categories(null, "顶级分类"));
                    }
                }
            }
        }
    }

    @RequestMapping("/seller/categories/delete")
    public View delete(Integer id) {
        Assert.notNull(id, "id错误");
        boolean hasChildren = categoriesService.criteria().andEqual("pid", id).exists();
        Assert.state(!hasChildren, "请先删除子类");
        Categories one = categoriesService.getOne(id);
        Assert.notNull(one, "不存在");
        Assert.state(one.getPid() != null, "不能删除顶级分类");
        categoriesService.delete(one);
        return new JsonMap();
    }


    @RequestMapping("/seller/categories/list")
    public View list() {
        checkRoot();
        List<Categories> list = categoriesService.criteria().getList();
        Map<Integer, Categories> map = new HashMap<>();
        for (Categories c : list) {
            map.put(c.getId(), c);
        }

        ArrayList<Categories> result = new ArrayList<>();
        for (Categories c : list) {
            if (c.getPid() == null) {
                result.add(c);
            } else {
                Categories pNode = map.get(c.getPid());
                List<Categories> children = pNode.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                    pNode.setChildren(children);
                }
                children.add(c);
            }
        }
        return new JsonVO(result);

    }

    @RequestMapping("/seller/categories/tree/easyui")
    public View easyUiTree() {

        checkRoot();
        List<Categories> categories = categoriesService.criteria().getList();

        List<EasyuiTreeNode> list = new ArrayList<>();
        Map<Integer, EasyuiTreeNode> map = new HashMap<>();

        for (Categories category : categories) {
            EasyuiTreeNode node = new EasyuiTreeNode(category.getId().toString(), category.getName());
            map.put(category.getId(), node);
        }

        for (Categories category : categories) {
            Integer pid = category.getPid();
            if (pid == null) {
                list.add(map.get(category.getId()));
            } else {
                map.get(pid).getChildren().add(map.get(category.getId()));
            }
        }


        return new JsonVO(list);
    }
}
