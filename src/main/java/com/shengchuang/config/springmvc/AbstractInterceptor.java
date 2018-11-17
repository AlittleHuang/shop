package com.shengchuang.config.springmvc;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

public abstract class AbstractInterceptor implements HandlerInterceptor, Comparable<AbstractInterceptor> {

    protected int orderNumber = 0;

    public abstract List<String> gatPathPatterns();

    @Override
    public int compareTo(@NotNull AbstractInterceptor other) {
        return this.orderNumber - other.orderNumber;
    }

}
