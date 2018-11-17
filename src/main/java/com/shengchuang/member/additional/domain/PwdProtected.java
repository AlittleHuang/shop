package com.shengchuang.member.additional.domain;

import javax.persistence.*;

/**
 * 密保问题
 */
@Entity(name = "pwd_protected")
public class PwdProtected {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    /**
     * 问题类型
     */
    private Integer type;

    /**
     * 问题答案
     */
    private String answer;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Integer userId;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
