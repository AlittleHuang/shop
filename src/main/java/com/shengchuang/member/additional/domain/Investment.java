package com.shengchuang.member.additional.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * @author
 */
@Entity(name = "investment")
public class Investment {
    public static final int STATUS_0 = 0;
    public static final int STATUS_1 = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    /**
     * 托管等级
     */
    private Integer type;

    /**
     * 关联会员id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 托管金额
     */
    private Double amount;

    /**
     * 状态(0收益期  1无收益期)
     */
    private Integer status;


    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    private Date expireTime;

    /**
     * 托管时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    ///////////////////////////////////////////////////////////////
    /**
     * 托管天数
     */
    @Transient
    private int cycleDays;

    /**
     * 剩余天数
     */
    @Transient
    private int remainDays;

    /**
     * 静态奖利率
     */
    @Transient
    private Double staticRate;

    public int getCycleDays() {
        return cycleDays;
    }

    public void setCycleDays(int cycleDays) {
        this.cycleDays = cycleDays;
    }

    public int getRemainDays() {
        return remainDays;
    }

    public void setRemainDays(int remainDays) {
        this.remainDays = remainDays;
    }

    public Double getStaticRate() {
        return staticRate;
    }

    public void setStaticRate(Double staticRate) {
        this.staticRate = staticRate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}