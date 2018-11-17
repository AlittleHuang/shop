package com.shengchuang.member.trading.domain;

import com.shengchuang.member.core.domain.PayInfo;
import com.shengchuang.member.core.domain.User;

import javax.persistence.*;
import java.util.Date;

/**
 * 积分交易订单
 */
@Entity(name = "trade_order")
public class TradeOrder {

    /**
     * 交易中
     */
    public static final int TRADING = 0;


    public static final int CONFIRM_BUYER = 0;

    public static final int CONFIRM_SELLER = 1;


    /**
     * 取消交易
     */
    public static final int CANCELED = -1;
    public static final int SUCCESS = 3;


    @Id
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    /**
     * 购买时间
     */
    private Date time;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 购买数量
     */
    private Double amount;

    /**
     * 购买价格
     */
    @Column(name = "pay_money")
    private Double payMoney;

    /**
     * 交易状态
     */
    private Integer status;

    /**
     * 关联挂卖订单
     */
    @Column(name = "tradingId")
    private Integer tradingId;

    @ManyToOne
    @JoinColumn(name = "tradingId", updatable = false, insertable = false)
    private Trading trading;

    @Column(name = "pay_proof")
    private String payProof;

    @Transient
    private PayInfo payInfo;

    /**
     * 积分类型
     */
    private Integer type;

    /**
     * 买家
     */
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User buyer;

    public TradeOrder() {
    }

    public TradeOrder(Integer userId, Double amount, Double payMoney, PayInfo payInfo, Integer type, Integer tradingId) {
        this.userId = userId;
        this.amount = amount;
        this.payMoney = payMoney;
        Date now = new Date();
        this.time = now;
        this.updateTime = now;
        this.payInfo = payInfo;
        this.type = type;
        this.tradingId = tradingId;
        this.status = TRADING;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Double payMoney) {
        this.payMoney = payMoney;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public PayInfo getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(PayInfo payInfo) {
        this.payInfo = payInfo;
    }

    public Integer getTradingId() {
        return tradingId;
    }

    public void setTradingId(Integer tradingId) {
        this.tradingId = tradingId;
    }

    public String getPayProof() {
        return payProof;
    }

    public void setPayProof(String payProof) {
        this.payProof = payProof;
    }

    public Trading getTrading() {
        return trading;
    }

    public void setTrading(Trading trading) {
        this.trading = trading;
    }
}
