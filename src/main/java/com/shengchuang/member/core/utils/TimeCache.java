package com.shengchuang.member.core.utils;


import lombok.Setter;
import org.springframework.util.Assert;

import java.util.function.Supplier;

/**
 * 按时间缓存
 * @param <T>
 */
public class TimeCache<T> {

    /**
     * 过期时刻
     */
    private long endTime;

    /**
     * 缓存时长(毫秒)
     */
    @Setter
    private long keepTime;

    private T data;

    private Supplier<T> getter;

    /**
     * 构造
     *
     * @param keepTime 缓存时长(毫秒)
     * @param getter   加载方法
     */
    public TimeCache(long keepTime, Supplier<T> getter) {

        Assert.state(keepTime > 0, "keepTime必须大于0");
        Assert.notNull(getter, "getter 不能为空");

        this.keepTime = keepTime;
        this.getter = getter;

    }

    public T get() {

        if (System.currentTimeMillis() > endTime) {
            synchronized (this) {
                if (System.currentTimeMillis() > endTime) {
                    data = getter.get();
                    endTime = System.currentTimeMillis() + keepTime;
                }
            }
        }

        return data;
    }

}
