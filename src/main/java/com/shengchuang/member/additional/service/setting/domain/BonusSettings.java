package com.shengchuang.member.additional.service.setting.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BonusSettings {

    /**
     * 矿机价格
     */
    public int[] miningTypeCost = {0, 500, 2000, 5000, 10000, 50000};

    /**
     * 分享奖
     */
    public double ztRate = 0.18;

    /**
     * 矿场主奖励
     */
    public double[] kcRate = {.0, .26, .27, .28, .31, .32, .33, .34, .35, .36};

    /**
     * 矿场主奖励手续费
     */
    public double kcFees = 0.2;

    /**
     * 社区管理奖励（日收益）
     */
    public double[] sqRate = {.0, .01, .02, .03, .04, .05, .06, .07, .08, .09};

    /**
     * 矿机日收益率
     */
    public double[] miningRate = {0, .006, .0065, .007, .008, .0085};

    /**
     * 矿机有效期
     */
    public int miningDuration = 365;
}
