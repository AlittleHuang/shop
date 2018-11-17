package com.shengchuang.member.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "balance_setting")
public class BalanceSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 币种类型
     */
    private Integer type;

    /**
     * 可提现
     */
    @Column(name = "withdraw_able")
    private boolean withdrawAble;

    /**
     * 可充值
     */
    @Column(name = "recharge_able")
    private boolean rechargeAble;

    /**
     * 价格
     */
    private Double price;

    private Double withdrawMax;
    private Double withdrawMin;
    private Double withdrawFactor;
    private Double withdrawFees;

    private Double rechargeMax;
    private Double rechargeMin;
    private Double rechargeFactor;
    private Double rechargeFees;
}
