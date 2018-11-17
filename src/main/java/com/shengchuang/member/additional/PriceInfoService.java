package com.shengchuang.member.additional;


import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.member.additional.service.setting.domain.Settings;
import com.shengchuang.common.util.HttpClientUtil;
import com.shengchuang.common.util.JsonUtil;
import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.common.util.TimeUtil;
import org.apache.http.client.methods.HttpGet;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceInfoService {

    public static String[] markets = {"BTC/QC", "LTC/QC", "ETH/QC", "USDT/QC",};
    public static String[] names = {"BTC", "LTC", "ETH", "USDT"};
    public static List<String> marketList = Arrays.asList(names);

    @Autowired
    private SettingsService settingsService;

    private String info;
    volatile private long updateTime;

    @NotNull
    private String priceInfo() {
        HttpGet httpGet = new HttpGet("http://www.zb.cn/");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.75 Safari/537.36");
        String x = HttpClientUtil.sendHttpRequest(httpGet, "UTF-8");
        String startStr = "JSON.parse('";
        String endStr = "\"}]')";
        int beginIndex = x.indexOf(startStr) + startStr.length();
        int endIndex = x.indexOf(endStr) + endStr.indexOf(']') + 1;
        return x.substring(beginIndex, endIndex);
    }

    public List<Map> allPriceInfo() {
        if (info == null || System.currentTimeMillis() > updateTime + 5 * TimeUtil.MILLIS_PER_MINUTE) {
            info = priceInfo();
            updateTime = System.currentTimeMillis();
        }
        List json = JsonUtil.decodeArray(info, HashMap.class);
        return (List<Map>)json;
    }

    /**
     * 实时价格信息
     *
     * @return key-> ; value->
     */
    public Map<String, Double> getPriceInfo() {

        List<Map> json = allPriceInfo();

        Map<String, Double> fees = new HashMap<>();

        for (int i = 0; i < json.size(); i++) {
            Map<String, Object> row = json.get(i);
            for (int j = 0; j < markets.length; j++) {
                Object market = row.get("market");
                if (markets[j].equals(market)) {
                    fees.put(names[j], Double.valueOf((String) row.get("sell1Price")));
                    break;
                }
            }
        }

        Settings settings = settingsService.getSettings();
        double factor = settings.getPriceOfUsdt() / fees.get("USDT");

        for (Map.Entry<String, Double> e : fees.entrySet()) {
            fees.put(e.getKey(), NumberUtil.halfUp(e.getValue() * factor, 8));
        }

        return fees;
    }

}
