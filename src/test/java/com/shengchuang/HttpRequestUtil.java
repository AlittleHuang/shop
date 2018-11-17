package com.shengchuang;

import com.alibaba.fastjson.JSONObject;
import com.shengchuang.common.http.request.PostJson;

public class HttpRequestUtil {

    public static void main(String[] args) {
        String body = "{\"orderNum\":\"1345\",\"productList\":[{\"productCount\":\"2\",\"productPoint\":\"80\"," +
                "\"productCode\":\"sp0898394XFt@#$\"},{\"productCount\":\"1\",\"productPoint\":\"70\",\"productCode\":\"" +
                "sp0898394XFt@#$%&13276389756\"}],\"mobileNumber\":\"admin\",\"type\":\"1\",\"pointNum\":\"230\"}";

        String sign = "BE6CC3228BBAB03A0500F2BDFD905E39";

        JSONObject result = new PostJson("http://localhost:8048/api/balance/opt/batch/zz")
                .addHeader("sign", sign)
                .getJsonObjResult();

        System.out.println(result);
    }


}
