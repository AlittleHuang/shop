package com.shengchuang.shop.web.model;

import lombok.experimental.Delegate;

import java.util.HashMap;
import java.util.Map;

public class ModelMap implements Map<String, Object> {

    @Delegate
    private final Map<String, Object> map;

    public ModelMap(Map<String, ?> map) {
        this.map = (Map<String, Object>) map;
    }

    public ModelMap() {
        this(new HashMap<>());
    }

    public ModelMap(String key, Object value) {
        this(new HashMap<>());
        put(key, value);
    }

    public ModelMap add(String key, Object value) {
        put(key, value);
        return this;
    }

}
