package com.shengchuang.member.trading.domain;

import com.shengchuang.member.core.domain.BalanceLog;
import com.shengchuang.member.core.domain.PayInfo;
import com.shengchuang.member.core.domain.User;

import javax.persistence.*;
import java.util.Date;

/**
 * 积分挂卖
 */
@Entity(name = "trading")
public class Trading {

    /**
     * 撤销订单
     */
    public static final int STATUS_CANCEL = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    @Version
    @Column(name = "OPTLOCK")
    private Long optlock;

    @Column(name = "user_id")
    private Integer userId;

    /**
     * 货币类型
     */
    private Integer type;

    /**
     * 数量
     */
    private Double amount;

    /**
     * 挂卖数量
     */
//    @Column(name = "total_amount")
//    private Double totalAmount;

    /**
     * 售卖时间
     */
    private Date time;

    /**
     * 保证金
     */
    //private Double margin;

    /**
     * 手续费
     */
    private Double fees;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pay_info_id")
    private PayInfo payInfo;

    @Transient
    private PayInfo weixinpay;

    @Transient
    private PayInfo alipay;

    /**
     * 最近一次发起交易时间
     */
    @Column(name = "trading_time")
    private Date tradingTime;

    /**
     * 最近一次交易成功
     */
    @Column(name = "traded_time")
    private Date tradedTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 排序字段
     */
    @Column(name = "`orders`")
    private Long orders;

    /** */
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "log_id")
    private BalanceLog log;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }
//
//    public Double getTotalAmount() {
//        return totalAmount;
//    }
//
//    public void setTotalAmount(Double totalAmount) {
//        this.totalAmount = totalAmount;
//    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTradingTime() {
        return tradingTime;
    }

    public void setTradingTime(Date tradingTime) {
        this.tradingTime = tradingTime;
    }

    public Date getTradedTime() {
        return tradedTime;
    }

    public void setTradedTime(Date tradedTime) {
        this.tradedTime = tradedTime;
    }

    public PayInfo getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(PayInfo payInfo) {
        this.payInfo = payInfo;
    }

    public PayInfo getWeixinpay() {
        return weixinpay;
    }

    public void setWeixinpay(PayInfo weixinpay) {
        this.weixinpay = weixinpay;
    }

    public PayInfo getAlipay() {
        return alipay;
    }

    public void setAlipay(PayInfo alipay) {
        this.alipay = alipay;
    }

    public BalanceLog getLog() {
        return log;
    }

    public void setLog(BalanceLog log) {
        this.log = log;
    }

    public Long getOrders() {
        return orders;
    }

    public void setOrders(Long orders) {
        this.orders = orders;
    }
}
