package com.shengchuang.member.core.domain.enmus;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.shengchuang.common.util.StreamUtil.asMap;

@Getter
public enum BalanceType {

    MMD("MMD"),//0

    INV("投资");


    private static final Map<Integer, BalanceType> INDEX_ORDINAL_MAP = asMap(values(), BalanceType::getIndex);

    public final String name;
    public final int index;

    BalanceType(String name) {
        this.name = name;
        this.index = ordinal();
    }

    public static BalanceType of(int index) {
        return INDEX_ORDINAL_MAP.get(index);
    }

    /**
     * 展示的类型
     */
    public static List<BalanceType> displays(){
        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList(MMD);
    }

}
