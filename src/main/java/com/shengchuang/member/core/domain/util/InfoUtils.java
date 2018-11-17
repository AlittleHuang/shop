package com.shengchuang.member.core.domain.util;

import com.alibaba.fastjson.JSONObject;
import com.shengchuang.common.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class InfoUtils {

    public static Map<String, Object> toInfoMap(String info) {
        Map<String, Object> infoMap = null;
        if (info != null) {
            if (info.startsWith("{") && info.endsWith("}")) {
                try {
                    JSONObject decode = JsonUtil.decode(info);
                    infoMap = decode;
                } catch (Exception e) {
                    infoMap = initInfomap(info);
                }
            } else {
                infoMap = initInfomap(info);
            }
        }
        return infoMap;
    }

    private static Map<String, Object> initInfomap(String info) {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("info", info);
        return infoMap;
    }
}
