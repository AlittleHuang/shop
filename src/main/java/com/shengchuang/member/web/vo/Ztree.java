package com.shengchuang.member.web.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Ztree implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String pId;

    private String name;

    private boolean open = false;

    private String icon;

    private Boolean checked;

    private List<Ztree> children;

    public Ztree(Object id, Object pId, Object name) {
        this.id = String.valueOf(id);
        this.pId = String.valueOf(pId);
        this.name = String.valueOf(name);
    }

    public Ztree addChild(Ztree child) {
        (children = children == null ? new ArrayList<>() : children).add(child);
        return this;
    }

}
