package com.shengchuang.member.trading.domain;


import com.shengchuang.member.core.domain.PayInfo;
import com.shengchuang.member.core.domain.User;

import javax.persistence.*;
import java.util.Date;

/**
 * 广告
 */
@Entity(name = "trading_ad")
public class TradingAd {

    public static final int TYPE_BUY = 0;//购买广告
    public static final int TYPE_SELL = 1;//出售广告

    /**
     * id
     */
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    /**
     * 锁
     */
    @Version
    @Column(name = "OPTLOCK")
    private Long optlock;

    @Column(name = "user_id")
    private Integer userId;

    private Integer count;

    /**
     * 交易已确认的总和
     */
    private Double traded;

    /**
     * 交易未确认的总和
     */
    private Double trading;

    private Double max;
    private Double min;
    private Integer type;//买或卖

    private Integer status;
    private Date time;

    private Double price;//价格

    @Column(name = "`order`")
    private Long order;

    /**
     * 诚信金
     */
    private Double fees;

    private Integer balanceType;//交易币种

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    private Integer payType;

    private String info;
    @Column(name = "limited_num")
    private Integer limitedNum;

    @Transient
    private PayInfo payInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getOptlock() {
        return optlock;
    }

    public void setOptlock(Long optlock) {
        this.optlock = optlock;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getTraded() {
        return traded;
    }

    public void setTraded(Double traded) {
        this.traded = traded;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getTrading() {
        return trading;
    }

    public void setTrading(Double trading) {
        this.trading = trading;
    }

    public void trad(double amount) {
        traded += amount;
        trading -= amount;
    }

    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public PayInfo getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(PayInfo payInfo) {
        this.payInfo = payInfo;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(Integer balanceType) {
        this.balanceType = balanceType;
    }

    public Integer getLimitedNum() {
        return limitedNum;
    }

    public void setLimitedNum(Integer limitedNum) {
        this.limitedNum = limitedNum;
    }
}
