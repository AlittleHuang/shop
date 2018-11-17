package com.shengchuang.member.additional.domain;

import com.shengchuang.member.core.domain.BalanceLog;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.Event;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 奖金
 */
@Data
@NoArgsConstructor
@Entity(name = "bonus")
public class Bonus {

    /**
     * 未结算
     */
    public static final int STATUS_NO = 0;

    /**
     * 已结算
     */
    public static final int STATUS_YES = 1;

    @Transient
    private static final String[] statusName = {"未结算", "已结算"};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    /**
     * 奖金数量
     */
    private double amount;

    /**
     * 结算状态
     */
    private Integer status;

    /**
     * 奖金类型 {@link BalanceLog#operation}
     */
    private Integer type;

    private Date time;

    @Transient
    private User user;

    @Column(name = "`update`")
    private Date update;

    private String info;

    @Transient
    private String typeName;

    public Bonus(int userId, double amount, Event event, int status, String info) {
        this.userId = userId;
        this.amount = amount;
        this.type = event.index;
        this.status = status;
        this.time = new Date();
        this.update = time;
        this.info = info;
    }

    public String getTypeName() {
        if (type == null) return null;
        return BalanceLog.getOperationName(type);
    }

    public String getStatusName() {
        if (status < 0 || status >= statusName.length)
            return "等级错误";
        return statusName[status];
    }

}
