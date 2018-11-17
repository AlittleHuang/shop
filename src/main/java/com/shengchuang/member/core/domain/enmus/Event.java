package com.shengchuang.member.core.domain.enmus;

import com.shengchuang.common.util.JsonUtil;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static com.shengchuang.common.util.StreamUtil.*;

@Getter
public enum Event implements JsonUtil.AbleToJsonString {

    /**
     * 兑换   0
     */
    CONVERTED("兑换"),

    /**
     * 转账   1
     */
    TRANSFER("转账"),

    /**
     * 充值   2
     */
    RECHARGE("充值"),

    /**
     * 提现   3
     */
    WITHDRAW("提现"),

    /**
     * 系统充值 4
     */
    RECHARGE_BY_ADMIN("资产充值"),

    /**
     * 购物   5
     */
    SHOPPING("购物"),

    /**
     * 交易   6
     */
    TRADE("交易"),

    /**
     * 诚信金  7
     */
    AD_FEES("诚信金"),

    ADD_MINING("新增矿机"),

    MINING("挖矿"),

    /**
     * 矿场主奖励
     */
    KCZ("矿场主奖励"),

    /**
     * 社区管理奖励
     */
    SQGL("社区管理奖励"),

    /**
     * 分享奖
     */
    FX("分享奖"),

    ;


    public static final List<Event> CAIWU_EVENTS = asUnmodifiableList(
            TRANSFER, RECHARGE, WITHDRAW, RECHARGE_BY_ADMIN, TRADE, AD_FEES, ADD_MINING, MINING, KCZ, SQGL
    );
    /**
     * 奖金
     */
    public static final List<Event> BONUS_EVENTS = asUnmodifiableList(KCZ,SQGL);
    /**
     * 充值
     */
    public static final List<Event> RECHARGE_EVENTS = asUnmodifiableList(RECHARGE_BY_ADMIN);
    /**
     * 奖金index
     */
    public static final List<Integer> BONUS_INDEXES = unmodifiableList(convert(BONUS_EVENTS, Enum::ordinal));

    /**
     * key:{@link Event#index}, value:{@link Event}
     */
    private static final Map<Integer, Event> INDEX_ORDINAL_MAP = asMap(values(), Event::getIndex);
    public final String name;
    public final int index;

    Event(String name) {
        this.name = name;
        this.index = ordinal();
    }

    Event(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static Event of(int index) {
        return INDEX_ORDINAL_MAP.get(index);
    }

}
