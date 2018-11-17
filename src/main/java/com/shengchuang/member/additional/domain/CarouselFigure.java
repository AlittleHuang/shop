package com.shengchuang.member.additional.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 轮播图
 *
 * @author HuangChengwei
 */

@Data
@NoArgsConstructor
@Entity(name = "carousel_figure")
public class CarouselFigure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    /**
     * 图片路径
     */
    @Column(name = "img_src")
    private String imgSrc;

    /**
     * 标题
     */
    private String title;

    /**
     * 优先级
     */
    @Column(name = "order_number")
    private Integer orderNumber;

    /**
     * 跳转链接
     */
    @Column(name = "href_url")
    private String hrefUrl;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime = new Date();

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 是否展示(0:否,1:是)
     */
    private Integer display;


}
