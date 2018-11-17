package com.shengchuang.member.additional.domain;

import javax.persistence.*;

/**
 * 条款
 */
@Entity(name = "clause")
public class Clause {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * html格式内容
     */
    @Lob
    private String html;

    /**
     * 文本格式内容
     */
    @Lob
    private String text;
    /**
     * 标题
     */
    private String title;

    /**
     * 类型
     */
    private Integer type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
