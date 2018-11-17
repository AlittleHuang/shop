package com.shengchuang.member.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "transfer_log")
public class TransferLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    /**
     * 转出金额
     */
    private Double amount;

    /**
     * 转出的记录
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "balance_log_id_from")
    private BalanceLog from;

    /**
     * 转入的记录
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "balance_log_id_to")
    private BalanceLog to;

    /**
     * 类型 0币种兑换, 1会员互转
     */
    @Column(name = "transfer_type", nullable = false)
    private Integer transferType;

    @Column(name = "poundage", nullable = false)
    private Double poundage;

    @Column(name = "price", nullable = false)
    private Double price;

    public TransferLog(double amount, BalanceLog from, BalanceLog to, int transferType, double poundage, double price) {
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.transferType = transferType;
        this.poundage = poundage;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BalanceLog getFrom() {
        return from;
    }

    public void setFrom(BalanceLog from) {
        this.from = from;
    }

    public BalanceLog getTo() {
        return to;
    }

    public void setTo(BalanceLog to) {
        this.to = to;
    }
}
