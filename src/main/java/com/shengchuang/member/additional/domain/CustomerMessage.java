package com.shengchuang.member.additional.domain;

import com.shengchuang.member.core.domain.User;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "customer_message")
public class CustomerMessage {

    /**
     * 奖项问题
     */
    public static final int TYPE_0 = 0;

    /**
     * 注册问题
     */
    public static final int TYPE_1 = 1;

    /**
     * 申诉问题
     */
    public static final int TYPE_2 = 2;

    /**
     * 其他问题
     */
    public static final int TYPE_3 = 3;

    public static final String[] TYPE_NAME = {"奖项问题", "注册问题", "申诉问题", "其他问题"};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "recipient_id")
    private Integer recipientId;

    private String title;

    private String content;

    private Integer type; //0奖项问题   1,注册问题 2,申诉问题  3,其他问题

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    private Integer status;

    @Transient
    private User user;

    @Transient
    private User sendder;

    public String getTypeName() {
        if (type == null || type < 0 || type >= TYPE_NAME.length)
            return "其他";
        String name = TYPE_NAME[type];
        return name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public User getSendder() {
        return sendder;
    }

    public void setSendder(User sendder) {
        this.sendder = sendder;
    }
}
