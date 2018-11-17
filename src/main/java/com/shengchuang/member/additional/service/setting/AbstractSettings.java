package com.shengchuang.member.additional.service.setting;

import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.BeanUtil;
import com.shengchuang.common.util.JsonUtil;
import com.shengchuang.common.util.StringUtil;
import com.shengchuang.member.core.domain.KeyValuePair;
import com.shengchuang.member.core.service.KeyValuePairService;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

public abstract class AbstractSettings<T> {
    protected final Class<T> settingsType;
    private final String keyPreFix;
    @Autowired
    protected KeyValuePairService keyValuePairDAO;
    protected T settings;

    public AbstractSettings(String keyPreFix, Class<T> settingsType) {
        this.keyPreFix = keyPreFix;
        this.settingsType = settingsType;
    }

    public static void main(String[] args) {
        String name = "avskdjfh";
        String key = name + 100;
        int index = Integer.valueOf(key.substring(name.length(), key.length()));
        System.out.println(index);
    }

    public void save(T settings) {
        this.settings = save(settings, settingsType);
    }

    public T refrushSettings() {
        if (settings == null) {
            try {
                settings = settingsType.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("创建" + settingsType + "实例时发生错误(可能缺少无参构造函数)", e);
            }
        }
        return get(settings, settingsType);
    }

    public T getSettings() {
        if (settings == null) {
            settings = refrushSettings();
        }
        return settings;
    }

    public void save(String key, String value) {
        keyValuePairDAO.save(new KeyValuePair(getKeyPreFix() + key, value));
    }

    public String get(String key) {
        try {
            Assert.notEmpty(key, "获取系统设置参数错误");
            KeyValuePair one = keyValuePairDAO.getOne(getKeyPreFix() + key);
            return one == null ? null : one.getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getInt(String key) {
        String s = get(key);
        return Integer.valueOf(s);
    }

    public Integer getInt(String key, Integer defaul) {
        try {
            return getInt(key);
        } catch (Exception e) {
            return defaul;
        }
    }

    public Double getDouble(String key) {
        return Double.valueOf(get(key));
    }

    public Double getDouble(String key, Double defaul) {
        try {
            return getDouble(key);
        } catch (Exception e) {
            return defaul;
        }
    }

    public Long getLong(String key) {
        return Long.valueOf(get(key));
    }

    public Long getLong(String key, Long defaul) {
        try {
            return getLong(key);
        } catch (Exception e) {
            return defaul;
        }
    }

    public BigDecimal getBigDecimal(String key) {
        String value = get(key);
        return value == null ? null : new BigDecimal(value);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaul) {
        try {
            return getBigDecimal(key);
        } catch (Exception e) {
            return defaul;
        }
    }

    public String getString(String key) {
        return get(key);
    }
/*
    @SuppressWarnings("unchecked")
    public <T> T get(Key<T> settingsKey, int index) {
        int i = index < 0 ? 0 : index;
        String subfix = index < 0 ? "" : "" + index;

        if (settingsKey == null) return null;
        String name = settingsKey.getName();
        T defaul = settingsKey.getDefaultValue();
        if (name == null) return settingsKey.defaultValue[i];
        Class<T> type = settingsKey.getType();

        if (Double.class.equals(type)) {
            return (T) getDouble(settingsKey.name + subfix, (Double) defaul);
        } else if (Long.class.equals(type)) {
            return (T) getLong(settingsKey.name + subfix, (Long) defaul);
        } else if (Integer.class.equals(type)) {
            return (T) getInt(settingsKey.name + subfix, (Integer) defaul);
        } else if (String.class.equals(type)) {
            return (T) getString(settingsKey.name + subfix, (String) defaul);
        } else if (BigDecimal.class.equals(type)) {
            return (T) getBigDecimal(settingsKey.name + subfix, (BigDecimal) defaul);
        }
        return defaul;
    }*/

    @SuppressWarnings("hiding")
    public <T> List<T> getList(Key<T> settingsKey) {
        List<String> keys = new ArrayList<>(settingsKey.defaultValue.length);
        String name = settingsKey.getName();
        for (int i = 0; i < settingsKey.defaultValue.length; i++) {
            keys.add(name + i);
        }
        List<KeyValuePair> keyValuePairs = keyValuePairDAO.findAllById(keys);
        for (KeyValuePair keyValuePair : keyValuePairs) {
            String key = keyValuePair.getKey();
            int index = Integer.valueOf(key.substring(name.length(), key.length()));
            settingsKey.defaultValue[index] = StringUtil.parse(settingsKey.type, keyValuePair.getValue());
        }
        return Arrays.asList(settingsKey.defaultValue);
    }

    public String getString(String key, String defaul) {
        String value = get(key);
        return value == null ? defaul : value;
    }

    protected <T> T save(T settings, Class<T> type) {
        List<String> propertyName = BeanUtil.propertyName(type);
        propertyName.remove("calss");
        List<KeyValuePair> keyValuePairList = new ArrayList<>(propertyName.size());
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(settings);
        for (String key : propertyName) {
            if (!beanWrapper.isReadableProperty(key) || !beanWrapper.isWritableProperty(key))
                continue;
            Object nValue = beanWrapper.getPropertyValue(key);
            if (nValue != null) {
                key = getKeyPreFix() + key;
                keyValuePairList.add(new KeyValuePair(key, JsonUtil.encode(nValue)));
            }
        }
        keyValuePairDAO.saveAll(keyValuePairList);
        return settings;
    }

    @SuppressWarnings("hiding")
    protected <T> T get(T t, Class<T> type) {
        List<String> propertyName = BeanUtil.propertyName(type);
        Map<String, String> fix_key = new HashMap<>();
        for (String key : propertyName) {
            fix_key.put(getKeyPreFix() + key, key);
        }
        List<KeyValuePair> list = keyValuePairDAO.findAllById(fix_key.keySet());
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(t);
        for (KeyValuePair keyValuePair : list) {
            String key = fix_key.get(keyValuePair.getKey());
            if (!beanWrapper.isWritableProperty(key)) continue;
            String valeu = keyValuePair.getValue();
            if (valeu == null) continue;
            Class<?> propertyType = beanWrapper.getPropertyType(key);
            Object decode = JsonUtil.decode(valeu, propertyType);
            beanWrapper.setPropertyValue(key, decode);
        }
        return t;
    }

    private String getKeyPreFix() {
        return keyPreFix;
    }

    public static class Key<T> {
        private final String name;
        private final T[] defaultValue;
        private final Class<T> type;

        @SafeVarargs
        public Key(Class<T> type, String name, T... defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public Class<T> getType() {
            return type;
        }

        public T getDefaultValue() {
            return defaultValue[0];
        }

        public T getDefaultValue(int index) {
            return defaultValue[index];
        }

    }
}
