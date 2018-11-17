package com.shengchuang.member.additional.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 奖金
 */
@Data
@NoArgsConstructor
@Entity(name = "price_log")
public class PriceLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    /**
     * 实时价格
     */
    private double price;

    /**
     * 保存日期
     */
    private Date time;


    public PriceLog(double price, Date time) {
        this.price = price;
        this.time = time;
    }

}
