package com.shengchuang.member.additional.domain;

import com.shengchuang.common.util.NumberUtil;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class RedPacket {

    /**
     * 未领取
     */
    public static final int STATUS_NO = 0;
    /**
     * 已领取
     */
    public static final int STATUS_YES = 1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 奖金数量
     */
    private double amount;
    /**
     * 结算状态
     */
    private Integer status;
    /**
     * 用户id
     */
    private Integer userId;
    private Date createTime;
    private Date updateTime;

    public RedPacket() {
    }

    public RedPacket(Integer userId, int x, int y) {
        this.userId = userId;
        this.amount = NumberUtil.nextDouble(x, y);
        //this.amount = x + Math.random() * y % (y - x + 1)>y?x + Math.random() * y % (y - x + 1)-1:x + Math.random() * y % (y - x + 1);
        this.createTime = new Date();
        this.status = STATUS_NO;

    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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
}
