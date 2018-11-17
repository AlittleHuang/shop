package com.shengchuang.member.core.domain;

import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.domain.enmus.Event;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 转账参数
 */
@Data
@NoArgsConstructor
//@Accessors(chain = true)
@Entity(name = "transfer_settings")
public class TransferSetting {

    /**
     * transferType-币种兑换
     */
    public static final int CONVERT = 0;

    /**
     * transferType-会员互转
     */
    public static final int TRANSFER = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    /**
     * 转入类型
     */
    @Column(nullable = false)
    private Integer typeIn;

    /**
     * 转出类型
     */
    @Column(nullable = false)
    private Integer typeOut;


    /**
     * 类型 0币种兑换, 1会员互转
     */
    @Column(name = "transfer_type", nullable = false)
    private Integer transferType;

    /**
     * 转出1(元)的手续费
     */
    private Double fees;

    /**
     * 最大金额
     */
    private Double max;

    /**
     * 最小金额
     */
    private Double min;

    /**
     * 倍数(checkAmount必须是factor的整数倍)
     */
    private Double factor;

    /**
     * 额度百分比
     */
    private Double proportion;

    /**
     * 转出比率(转出1元收得到的数量)
     */
    private Double price;

    /**
     * 每日限额
     */
    private Double dailyLimit;

    /**
     * 每日次数限制
     */
    private Integer dailyFrequency;

    @Transient
    private boolean edit;

    public void checkAmount(double amount) {
        Assert.state(max == null || amount <= max, "数量不能大于" + max);
        Assert.state(min == null || amount >= min, "数量不能小于" + min);
        Assert.state(factor == null || amount % factor == 0, "数量必须是" + factor + "倍数");
    }

    /**
     * 计算手续费
     */
    public double feesAmount(double amout) {
        return (fees == null ? 0 : fees) * amout;
    }

    /**
     * 计算到账数量
     */
    public double in(double amount) {
        return (price == null ? 1 : price) * amount;
    }

    /**
     * 转换成{@link Event}
     */
    public Event toBalanceLogType() {
        return transferType == CONVERT ? Event.CONVERTED : Event.TRANSFER;
    }

    public BalanceType typeOut() {
        return BalanceType.of(typeOut);
    }

    public BalanceType typeIn() {
        return BalanceType.of(typeIn);
    }

}
