package com.shengchuang.shop.web.model;

import lombok.Getter;

import java.util.List;

@Getter
public class EasyUiGrid<T> {

    List<T> rows;
    Long total;

    public EasyUiGrid(List<T> rows, Long total) {
        this.rows = rows;
        this.total = total;
    }
}
