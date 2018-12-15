package com.shengchuang.shop.web.model;

import com.alibaba.fastjson.JSONObject;
import lombok.experimental.Delegate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class WebPageRequest {

    private JSONObject param;
    private HttpServletRequest request;

    public WebPageRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Delegate
    public JSONObject getParam() {
        if (param == null && request != null) {
            param = new JSONObject();
            for (Map.Entry<String, String[]> e : request.getParameterMap().entrySet()) {
                String[] value = e.getValue();
                param.put(e.getKey(), value.length == 1 ? value[0] : value);
            }
        }
        return param;
    }
}
