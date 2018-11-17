package com.shengchuang.member.core.domain;

import com.shengchuang.member.core.domain.enmus.BalanceType;

import javax.persistence.*;
import java.util.Date;

/**
 * 提现订单
 *
 * @author HuangChengwei
 */
@Entity(name = "withdraw")
public class Withdraw {

    /**
     * 待审核
     */
    public static final int STATUS_0 = 0;

    /**
     * 审核通过
     */
    public static final int STATUS_1 = 1;

    /**
     * 审核不通过
     */
    public static final int STATUS_2 = 2;

    private static final String[] statusName = {
            "受理中", "成功", "审核不通过"
    };

    @Transient
    String username;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;
    /**
     * 关联用户id
     */
    @Column(name = "user_id")
    private Integer userId;
    /**
     * 币种类型
     */
    @Column(name = "balance_type")
    private Integer balanceType;
    /**
     * 充值金额
     */
    private Double amount;

    /**
     * 提币地址
     */
    private String addr;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @Column(name = "uodate_time")
    private Date updateTime;
    /**
     * 备注信息
     */
    private String info;
    /**
     * 审核信息
     */
    @Column(name = "re_info")
    private String reInfo;
    /**
     * 手续费比例
     */
    private Double feerate;
    /**
     * 手续费
     */
    private Double fee;
    /**
     * 实际到账
     */
    private Double tramount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pay_info_id")
    private PayInfo payInfo;

    public void setFee(Double amount) {
        this.fee = amount*this.feerate;
    }
    public Double getFee() {
        return fee;
    }

    public Double getTramount() {
        return tramount;
    }

    public void setTramount(Double amount) {
        this.tramount = amount-this.getFee();
    }


    public String getStatusname() {
        return statusName[status];
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amoune) {
        this.amount = amoune;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getReInfo() {
        return reInfo;
    }

    public void setReInfo(String reInfo) {
        this.reInfo = reInfo;
    }

    public Integer getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(Integer balanceType) {
        this.balanceType = balanceType;
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

    public PayInfo getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(PayInfo payInfo) {
        this.payInfo = payInfo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTypeName() {
        return BalanceType.of(balanceType).name;
    }

    public Double getFeerate() {
        return feerate;
    }

    public void setFeerate(Double feerate) {
        this.feerate = feerate;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
