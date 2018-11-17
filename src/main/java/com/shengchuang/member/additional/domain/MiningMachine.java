package com.shengchuang.member.additional.domain;

import com.shengchuang.common.util.TimeUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 矿机
 */
@Data
@NoArgsConstructor
@Entity(name = "mining_machine")
public class MiningMachine {

    public static String[] names = {
            "", "M1", "M2", "M3", "M4", "M5",
    };

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;

    /**
     * 关联userId
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 购买时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 到期时间
     */
    @Column(name = "end_time")
    private Date endTime;

    /**
     * 消耗DMM数量
     */
    private Double amount;

    /**
     *
     */
    private Integer level;

    /**
     * 状态 0->工作中,1->废弃
     */
    private Integer status;

    /**
     * @param userId   用户ID
     * @param duration 有效期 (天)
     */
    public MiningMachine(int userId, int level, double amount, int duration) {
        this.userId = userId;
        createTime = new Date();
        this.level = level;
        this.amount = amount;
        endTime = TimeUtil.addDay(createTime, duration);
        status = 0;
    }
}
