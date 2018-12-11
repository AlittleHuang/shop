package com.shengchuang.shop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegionsService {

    @Getter
    @Setter
    public static class Node {
        private int level;
        private int id;
        private String name;
        private Integer pid;

        public Node(int id, String name, Integer pid, int level) {
            this.id = id;
            this.name = name;
            this.pid = pid;
            this.level = level;
        }
    }

    @Getter
    private static String data;
    private static Map<Integer, Node> dataMap = new HashMap<>();

    private static void setNode(JSONObject node, Integer pid, int level) {
        int code = node.getIntValue("code");
        dataMap.put(code, new Node(code, node.getString("name"), pid, level));
        JSONArray children = node.getJSONArray("children");
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                setNode(children.getJSONObject(i), code, level + 1);
            }
        }
    }

    static {
        try (InputStream inputStream = new ClassPathResource("data/pca-code.json").getInputStream()) {
            data = IOUtils.toString(inputStream, "utf-8");
            JSONArray array = JSON.parseArray(data);
            for (int i = 0; i < array.size(); i++) {
                setNode(array.getJSONObject(i), null, 0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasArea(Integer id) {
        if (id == null) return false;
        Node node = dataMap.get(id);
        return node != null && node.level == 2;
    }


    public static List<Node> getProvinceCitArea(Integer areaId) {//省市区
        List<Node> nodes = new ArrayList<>();
        Node node = dataMap.get(areaId);
        while (node != null) {
            nodes.add(node);
            node = dataMap.get(node.getPid());
        }
        return nodes;
    }

    public static String toString(Integer areaId) {//省市区
        List<Node> nodes = new ArrayList<>();
        Node node = dataMap.get(areaId);
        StringBuilder result = new StringBuilder();
        while (node != null) {
            result.insert(0, node.getName());
            node = dataMap.get(node.getPid());
        }
        return result.toString();
    }

}
