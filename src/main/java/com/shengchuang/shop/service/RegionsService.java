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
import java.util.HashMap;
import java.util.Map;

@Service
public class RegionsService {

    @Getter
    @Setter
    public static class Node {
        int id;
        String name;
        Integer pid;

        public Node(int id, String name, Integer pid) {
            this.id = id;
            this.name = name;
            this.pid = pid;
        }
    }

    @Getter
    private String data;
    private Map<Integer, Node> dataMap = new HashMap<>();

    private void setNode(JSONObject node, Integer pid) {
        int code = node.getIntValue("code");
        dataMap.put(code, new Node(code, node.getString("name"), pid));
        JSONArray children = node.getJSONArray("children");
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                setNode(children.getJSONObject(i), code);
            }
        }
    }

    {
        try (InputStream inputStream = new ClassPathResource("data/pca-code.json").getInputStream()) {
            data = IOUtils.toString(inputStream, "utf-8");
            JSONArray array = JSON.parseArray(data);
            for (int i = 0; i < array.size(); i++) {
                setNode(array.getJSONObject(i), null);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
