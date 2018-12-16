package com.shengchuang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;

public class Main {

    public static String[] markets = {"BTC/QC", "LTC/QC", "ETH/QC", "USDT/QC",};
    public static String[] names = {"BTC", "LTC", "ETH", "USDT"};

    public static void main(String[] args) throws IOException {
//        InputStream inputStream = new ClassPathResource("data/test.json").getInputStream();
//        String str = IOUtils.toString(inputStream, "utf-8");
//        System.out.println(str);
        JSONObject object = new JSONObject();
        object.put("x","y");
        Integer x = object.getObject("x", Integer.class);
        System.out.println(x);
    }

    static class ObservableX extends Observable {
        @Override
        protected synchronized void setChanged() {
            super.setChanged();
        }
    }
}
