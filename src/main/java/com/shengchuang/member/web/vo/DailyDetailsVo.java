package com.shengchuang.member.web.vo;

import com.shengchuang.common.util.TimeUtil;

import java.time.LocalDate;
import java.util.Date;

public class DailyDetailsVo {

    public static final Date END_DAY = TimeUtil.getStartTimeToday();

    /**
     * 日期
     */
    private LocalDate days;

    /**
     * 每天挂卖的总数量
     */
    private Double sellAmount;

    /**
     * 每天交易完成的数量
     */
    private Double buyAmount;

    public DailyDetailsVo(LocalDate days, Double sellAmount, Double buyAmount) {
        this.days = days;
        this.sellAmount = sellAmount;
        this.buyAmount = buyAmount;
    }

    public static Date getEndDay() {
        return END_DAY;
    }

    public LocalDate getDays() {
        return days;
    }

    public void setDays(LocalDate days) {
        this.days = days;
    }

    public Double getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(Double sellAmount) {
        this.sellAmount = sellAmount;
    }

    public Double getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(Double buyAmount) {
        this.buyAmount = buyAmount;
    }
}
