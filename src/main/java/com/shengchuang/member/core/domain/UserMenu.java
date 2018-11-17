package com.shengchuang.member.core.domain;

import javax.persistence.*;

/**
 * 管理员菜单关联
 *
 * @author Administrator
 */
@Entity
@Table(name = "user_menu")
public class UserMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id; //主键

    @Column(name = "user_id")
    private Integer userId; //用户Id

    @Column(name = "menu_id")
    private Integer menuId; //菜单Id

    public UserMenu() {
    }

    public UserMenu(Integer userId, Integer menuId) {
        this.userId = userId;
        this.menuId = menuId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }
}
