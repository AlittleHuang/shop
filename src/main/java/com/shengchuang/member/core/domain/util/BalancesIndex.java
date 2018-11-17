package com.shengchuang.member.core.domain.util;

import com.shengchuang.common.util.StreamUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class BalancesIndex implements StreamUtil, Iterable<Balance>, BiFunction<Integer, BalanceType, Balance> {

    private final List<Balance> balances;
    private Map<Integer, Map<BalanceType, Balance>> userIdBalanceMap;
    private Map<BalanceType, Map<Integer, Balance>> typeBalanceMap;

    public BalancesIndex(@NotNull List<Balance> balances) {
        Objects.requireNonNull(balances);
        this.balances = balances;
    }

    public Balance get(int userId, BalanceType balanceType) {
        return getBalance(userId, balanceType);
    }

    public double getAmount(int userId, BalanceType balanceType) {
        Balance balance = get(userId, balanceType);
        return balance == null ? 0d : balance.getAmount();
    }

    public double sum(int userId, BalanceType... balanceTypes) {
        double sum = 0d;
        for (BalanceType type : balanceTypes) {
            sum += getAmount(userId, type);
        }
        return sum;
    }


    public BalancesIndex add(Balance balance) {
        balances.add(balance);
        if (userIdBalanceMap != null) {
            userIdBalanceMap.computeIfAbsent(balance.getUserId(), k -> new HashMap<>())
                    .put(balance.type(), balance);
        }
        if (typeBalanceMap != null) {
            typeBalanceMap.computeIfAbsent(balance.type(), k -> new HashMap<>())
                    .put(balance.getUserId(), balance);
        }
        return this;
    }

    public Balance computeIfAbsent(int userId,
                                   BalanceType balanceType,
                                   BiFunction<Integer, BalanceType, Balance> mappingFunction) {
        Balance balance = get(userId, balanceType);
        if (balance == null) {
            balance = mappingFunction.apply(userId, balanceType);
            if (balance != null) {
                add(balance);
            }
        }
        return balance;
    }

    public Collection<Balance> getByUserId(int userId) {
        Map<BalanceType, Balance> map = getUserIdBalanceMap().get(userId);
        return map == null ? Collections.emptyList() : map.values();
    }

    public Collection<Balance> getByType(BalanceType type) {
        Map<Integer, Balance> map = getTypeBalanceMap().get(type);
        return map == null ? Collections.emptyList() : map.values();
    }

    public Balance getOneById(int userId) {
        Collection<Balance> list = getByUserId(userId);
        Assert.state(list.size() <= 1, "ont only one");
        return list.isEmpty() ? null : list.iterator().next();
    }

    public Balance getOneByType(BalanceType type) {
        Collection<Balance> list = getByType(type);
        Assert.state(balances.size() <= 1, "ont only one");
        return list.isEmpty() ? null : list.iterator().next();
    }

    @NotNull
    @Override
    public Iterator<Balance> iterator() {
        return balances.iterator();
    }

    @NotNull
    public Stream<Balance> stream() {
        return balances.stream();
    }

    private Balance getBalance(int userId, BalanceType balanceType) {
        if (userIdBalanceMap != null)
            return getBalanceFromMap(userIdBalanceMap, userId, balanceType);
        if (typeBalanceMap != null)
            return getBalanceFromMap(typeBalanceMap, balanceType, userId);
        return getBalanceFromMap(getUserIdBalanceMap(), userId, balanceType);
    }

    private Map<Integer, Map<BalanceType, Balance>> getUserIdBalanceMap() {
        if (userIdBalanceMap == null) {
            userIdBalanceMap = map(Balance::getUserId, Balance::type);
        }
        return userIdBalanceMap;
    }

    public Map<BalanceType, Map<Integer, Balance>> getTypeBalanceMap() {
        if (typeBalanceMap == null) {
            typeBalanceMap = map(Balance::type, Balance::getUserId);
        }
        return typeBalanceMap;
    }

    @NotNull
    private <K1, K2> Map<K1, Map<K2, Balance>> map(Function<Balance, K1> getKey1, Function<Balance, K2> getKey2) {
        Map<K1, Map<K2, Balance>> map = new HashMap<>();
        for (Balance balance : balances) {
            K1 k1 = getKey1.apply(balance);
            map.computeIfAbsent(k1, k -> new HashMap<>())
                    .put(getKey2.apply(balance), balance);
        }
        return map;
    }

    private <K1, K2> Balance getBalanceFromMap(Map<K1, Map<K2, Balance>> map, K1 k1, K2 k2) {
        Map<K2, Balance> k2Map = map.get(k1);
        if (k2Map != null)
            return k2Map.get(k2);
        return null;
    }

    @Override
    public Balance apply(Integer userId, BalanceType balanceType) {
        return get(userId, balanceType);
    }
}
