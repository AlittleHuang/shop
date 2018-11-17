package com.shengchuang.member.additional.domain;


import javax.persistence.*;
import java.util.Date;

/**
 * 新闻公告
 */
@Entity(name = "inform")
public class Inform {

    /**
     * 矿协通知
     */
    public static final int TYPE_0 = 0;

    /**
     * 行业新闻
     */
    public static final int TYPE_1 = 1;

    @Transient
    public static final String[] TYPE_NAME = {"矿协通知", "行业新闻"};
    @Transient
    private static final String[] typeName = TYPE_NAME;

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
     * 时间
     */
    private Date time;

    /**
     * 标题
     */
    private String title;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 状态
     */
    private Integer status;
    /**
     * 上一条记录
     */
    @Transient
    private Integer upId;
    @Transient
    private String upTitle;
    /**
     * 下一条记录
     */
    @Transient
    private Integer downId;
    @Transient
    private String downTitle;
    @Transient
    private String url;

    public String getTypeName() {
        if (type != null && type >= 0 && type < TYPE_NAME.length)
            return TYPE_NAME[type];
        return "其他";
    }

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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUpId() {
        return upId;
    }

    public void setUpId(Integer upId) {
        this.upId = upId;
    }

    public Integer getDownId() {
        return downId;
    }

    public void setDownId(Integer downId) {
        this.downId = downId;
    }

    public String getUpTitle() {
        return upTitle;
    }

    public void setUpTitle(String upTitle) {
        this.upTitle = upTitle;
    }

    public String getDownTitle() {
        return downTitle;
    }

    public void setDownTitle(String downTitle) {
        this.downTitle = downTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
