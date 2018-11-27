package com.shengchuang.shop.web.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EasyuiTreeNode {

    private String id;
    private String text;
    private List<EasyuiTreeNode> children;

    public EasyuiTreeNode(String id, String text) {
        this.id = id;
        this.text = text;
        this.children = new ArrayList<>();
    }




}
