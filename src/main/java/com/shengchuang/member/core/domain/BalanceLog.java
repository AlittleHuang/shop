package com.shengchuang.member.core.domain;

import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.domain.enmus.Event;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 收支明细
 */
@Data
@NoArgsConstructor
@Entity(name = "balance_log")
@Table(indexes = {
        @Index(name = "user_id_index", columnList = "user_id"),
        @Index(name = "type_index", columnList = "type"),
        @Index(name = "user_id_and_type_index", columnList = "user_id,type"),
})
public class BalanceLog {

    public static final List<String> OPERATION_NAME = Collections.unmodifiableList(
            Stream.of(Event.values()).map(Event::getName).collect(Collectors.toList())
    );

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    /**
     * 账户(积分)类型
     */
    private Integer type;

    /**
     * 收支金额
     */
    @Column(nullable = false)
    private Double amount;

    /**
     * 余额(改变后)
     */
    @Column(name = "current_balance")
    private Double currentBalance;

    /**
     * 备注
     */
    private String info;

    /**
     * 时间
     */
    private Date time;

    /**
     * 操作
     */
    private Integer operation;

    @Transient
    private User user;

    public BalanceLog(Integer userId,
                      Integer type,
                      Double amount,
                      Double currentBalance,
                      Integer operation,
                      String info) {
        super();
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.currentBalance = currentBalance;
        this.operation = operation;
        this.info = info;
        this.time = new Date();
    }

    public static List<Integer> bonusOpt() {
        return Event.BONUS_INDEXES;
    }

    public static String getTypeName(Integer type) {
        BalanceType balanceType = BalanceType.of(type);
        return balanceType == null ? "未知" : balanceType.getName();
    }

    public static String getOperationName(Integer operation) {
        if (operation == null) return "其他";
        Event event = Event.of(operation);
        return event == null ? "其他" : event.getName();
    }

    public Double getAmount() {
        if (amount == null) return null;
        amount = NumberUtil.halfUp(amount, 4);
        return amount;
    }

    public Double getCurrentBalance() {
        if (currentBalance == null) return null;
        return NumberUtil.halfUp(currentBalance, 4);
    }

    public String getTypeName() {
        return getTypeName(type);
    }

    public String getOperationName() {
        return getOperationName(operation);
    }

}
