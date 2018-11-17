package com.shengchuang.member.additional.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 */
@Entity(name = "statistical_balance")
public class StatisticalBalance {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    private Double balance0;// 云商会员码

    private Double balance1;// 购物积分

    private Double balance2;// 可用积分

    private Double balance3;// 注册积分

    private Double balance4;// 储存云宝

    private Double balance5;// 提取中云宝

    private Double balance6;// 流通云宝

    private Double balance7;// 可用分红

    private Double balance8;// 消费累计

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getBalance0() {
        return balance0;
    }

    public void setBalance0(Double balance0) {
        this.balance0 = balance0;
    }

    public Double getBalance1() {
        return balance1;
    }

    public void setBalance1(Double balance1) {
        this.balance1 = balance1;
    }

    public Double getBalance2() {
        return balance2;
    }

    public void setBalance2(Double balance2) {
        this.balance2 = balance2;
    }

    public Double getBalance3() {
        return balance3;
    }

    public void setBalance3(Double balance3) {
        this.balance3 = balance3;
    }

    public Double getBalance4() {
        return balance4;
    }

    public void setBalance4(Double balance4) {
        this.balance4 = balance4;
    }

    public Double getBalance5() {
        return balance5;
    }

    public void setBalance5(Double balance5) {
        this.balance5 = balance5;
    }

    public Double getBalance6() {
        return balance6;
    }

    public void setBalance6(Double balance6) {
        this.balance6 = balance6;
    }

    public Double getBalance7() {
        return balance7;
    }

    public void setBalance7(Double balance7) {
        this.balance7 = balance7;
    }

    public Double getBalance8() {
        return balance8;
    }

    public void setBalance8(Double balance8) {
        this.balance8 = balance8;
    }

}
