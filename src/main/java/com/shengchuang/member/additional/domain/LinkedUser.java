package com.shengchuang.member.additional.domain;

import javax.persistence.*;

/**
 * 关联账户
 *
 * @author HuangChengwei
 */
@Entity(name = "linked_user")
public class LinkedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 被关联用户id
     */
    @Column(name = "linked_id")
    private Integer linkedId;

    /**
     * 被关联用户账号
     */
    @Transient
    private String username;

    /**
     * 被关联用户登录密码
     */
    @Transient
    private String password;

    /**
     * 被关联用户二级密码
     */
    @Transient
    private String secondpwd;

    public LinkedUser() {
    }

    /**
     * @param userId
     * @param linkedId
     */
    public LinkedUser(Integer userId, Integer linkedId) {
        super();
        this.userId = userId;
        this.linkedId = linkedId;
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

    public Integer getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(Integer linkedId) {
        this.linkedId = linkedId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecondpwd() {
        return secondpwd;
    }

    public void setSecondpwd(String secondpwd) {
        this.secondpwd = secondpwd;
    }

}
