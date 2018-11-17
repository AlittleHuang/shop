package com.shengchuang.member.web.vo;

import java.util.*;

public class LayTreeNode {

    private String id;

    private String pId;

    private String name;

    private String alias;

    private List<LayTreeNode> children;

    public LayTreeNode() {
    }

    public LayTreeNode(String id, String pId, String name) {
        super();
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

    /**
     * 组织成树结构
     *
     * @param list
     * @return
     */
    public static List<LayTreeNode> organize2Tree(List<LayTreeNode> list) {
        if (list == null) {
            return null;
        }

        if (list.size() <= 1) {
            return new ArrayList<>(list);
        }

        List<LayTreeNode> trees = new LinkedList<LayTreeNode>();

        Map<String, LayTreeNode> map = new HashMap<String, LayTreeNode>();
        for (LayTreeNode layTree : list) {
            trees.add(layTree);
            map.put(layTree.getId(), layTree);
        }

        for (LayTreeNode node : list) {
            String pid = node.getpId();
            if (pid == null) {
                continue;
            }
            LayTreeNode pNode = map.get(pid);
            if (pNode == null) {
                continue;
            }
            List<LayTreeNode> pChildren = pNode.getChildren();
            if (pChildren == null) {
                pChildren = new ArrayList<>();
                pNode.setChildren(pChildren);
            }
            pChildren.add(node);
            trees.remove(node);
        }

        return trees;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<LayTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<LayTreeNode> children) {
        this.children = children;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

}
