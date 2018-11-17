package com.shengchuang.member.additional.service.setting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SpInfo {

    public static final int DEFAULT_SP_INITIAL = 3000000;

    /**
     * 拆分倍数
     */
    private BigDecimal splitatio;

    /**
     * 初始SP总数
     */
    private Integer spInitial;

    /**
     * 下一次拆分时间
     */
    private LocalDate nextSpliteTime;

    /**
     * 下一次拆分最终单价
     */
    private Double finalPrice;

    public BigDecimal getSplitatio() {
        return splitatio;
    }

    public void setSplitatio(BigDecimal splitatio) {
        this.splitatio = splitatio;
    }

    public Integer getSpInitial() {
        return spInitial;
    }

    public void setSpInitial(Integer spInitial) {
        this.spInitial = spInitial;
    }

    public LocalDate getNextSpliteTime() {
        return nextSpliteTime;
    }

    public void setNextSpliteTime(LocalDate nextSpliteTime) {
        this.nextSpliteTime = nextSpliteTime;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    /**
     * SP总量
     *
     * @return
     */
    public double spTotal() {
        return new BigDecimal(spInitial).multiply(splitatio).doubleValue();
    }
}
