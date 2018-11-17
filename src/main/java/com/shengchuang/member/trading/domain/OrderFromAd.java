package com.shengchuang.member.trading.domain;

import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.member.core.domain.PayInfo;
import com.shengchuang.member.core.domain.User;

import javax.persistence.*;
import java.util.Date;

/**
 * 交易订单
 */
@Entity(name = "order_from_ad")
public class OrderFromAd {

    public static final int CONFIRM_BUYER = 0;
    public static final int CONFIRM_SELLER = 1;
    public static final int CONFIRM_ADMIN = 2;


    /**
     * 取消状态
     */
    public static final int STATUS_CANCEL = -1;
    /**
     * 未确认
     */
    public static final int STATUS_T = 0;
    /**
     * 买家确认
     */
    public static final int STATUS_B = 1;
    /**
     * 卖家确认(成功)
     */
    public static final int STATUS_S = 2;
    /**
     * 交易成功
     */
    public static final int STATUS_SUCCESS = 3;
    public static final int TYPE_BUY = 0;
    public static final int TYPE_SELL = 1;
    /**
     * 买家
     */
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    User buyer;
    /**
     * 卖家
     */
    @ManyToOne
    @JoinColumn(name = "seller_id")
    User seller;
    /**
     * 广告
     */
    @ManyToOne
    @JoinColumn(name = "trading_ad_id")
    TradingAd tradingAd;
    /**
     * 主键
     */
    @Id
    private Integer id;
    /**
     * 锁
     */
    @Version
    @Column(name = "OPTLOCK")
    private Long optlock;
    @Column(name = "user_id")
    private Integer userId;
    /**
     * 交易数量
     */
    private Double amount;
    /**
     * 创建时间
     */
    private Date time;
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;
    /**
     * 买家打款时间
     */
    @Column(name = "buyer_sub_time")
    private Date buyerSubTime;
    /**
     * 卖家收款时间
     */
    @Column(name = "seller_sub_time")
    private Date sellerSubTime;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 诚信金
     */
    private Double deposit;
    /**
     * 手续费
     */
    private Double fees;
    /**
     * 价格
     */
    private Double price;

    @Transient
    private PayInfo payInfo;

    private String imgsrc;

    public OrderFromAd() {
    }

    public OrderFromAd(Double amount, User buyer, User seller, TradingAd tradingAd) {
        this.amount = amount;
        this.time = new Date();
        this.status = STATUS_T;
        this.buyer = buyer;
        this.seller = seller;
        this.tradingAd = tradingAd;
        id = NumberUtil.randomInt(8);
    }

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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public TradingAd getTradingAd() {
        return tradingAd;
    }

    public void setTradingAd(TradingAd tradingAd) {
        this.tradingAd = tradingAd;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public PayInfo getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(PayInfo payInfo) {
        this.payInfo = payInfo;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    public Date getSellerSubTime() {
        return sellerSubTime;
    }

    public void setSellerSubTime(Date sellerSubTime) {
        this.sellerSubTime = sellerSubTime;
    }

    public Date getBuyerSubTime() {
        return buyerSubTime;
    }

    public void setBuyerSubTime(Date buyerSubTime) {
        this.buyerSubTime = buyerSubTime;
    }

}
