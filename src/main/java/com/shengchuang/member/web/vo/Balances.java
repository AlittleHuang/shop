package com.shengchuang.member.web.vo;

import com.shengchuang.member.core.domain.User;

/**
 * 记录所有积分
 *
 * @author HuangChengwei
 */
public class Balances {

    private Integer userId;

    /**
     * 激活积分
     */
    private Double active;

    /**
     * 购物积分
     */
    private Double shoping;

    /**
     * 奖励积分
     */
    private Double gift;

    /**
     * 全返积分
     */
    private Double recome;

    private String username;

    private User user;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getActive() {
        return active;
    }

    public void setActive(Double active) {
        this.active = active;
    }

    public Double getShoping() {
        return shoping;
    }

    public void setShoping(Double shoping) {
        this.shoping = shoping;
    }

    public Double getGift() {
        return gift;
    }

    public void setGift(Double gift) {
        this.gift = gift;
    }

    public Double getRecome() {
        return recome;
    }

    public void setRecome(Double recome) {
        this.recome = recome;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
