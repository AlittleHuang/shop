package com.shengchuang.member.core.domain;

import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Random;

@Data
@NoArgsConstructor
@Entity
@Table(name = "balance", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "type"}))
public class Balance implements Serializable {

    /**
     * 最小变动金额,小于此数量不进行记录
     */
    public static final double MIN_CHANGE = 0.0001;
    private static final long serialVersionUID = 5956284067834539760L;
    private static final char[] chars = {
            '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Version
    @Column(name = "OPTLOCK")
    private Integer optlock;
    /**
     * 账户余额
     */
    @Column(nullable = false)
    private Double amount;
    /**
     * 保留额度( 支出时,余额不能小于lockedAmount )
     */
    @Column(name = "locked_amount", nullable = false)
    private Double lockedAmount;
    /**
     * 币种类型
     */
    @Column(nullable = false)
    private Integer type;

    /*---------- database fields ----------*///end
    /**
     * 关联用户id
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    @Transient
    private String typeName;

    public Balance(BalanceType type, Integer userId) {
        this.amount = 0d;
        this.lockedAmount = 0d;
        this.type = type.index;
        this.userId = userId;
    }

    @NotNull
    public static String randomAddr(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = NumberUtil.getRandom();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(58);
            sb.append(chars[index]);
        }
        return sb.toString();
    }

    public void setAmount(Double amount) {
        this.amount = amount == null ? null : NumberUtil.halfUp(amount, 6);//保留6位小数
    }

    public String getTypeName() {
        BalanceType balanceType = BalanceType.of(type);
        return balanceType == null ? "未知" : balanceType.getName();
    }

    public BalanceType type() {
        return BalanceType.of(type);
    }
}