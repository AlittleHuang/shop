package com.shengchuang.member.core.service;

import com.shengchuang.member.core.domain.Menu;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.UserMenu;
import com.shengchuang.member.core.repository.MenuRepository;
import com.shengchuang.member.core.repository.UserMenuRepository;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuService extends AbstractService<Menu, Integer> {

    @Autowired
    MenuRepository menuDAO;

    @Autowired
    UserMenuRepository userMenuDao;

    /* * * * * * * * * * * * * * * * * * * * * * * * */

    public List<Menu> menus(User user) {

        List<UserMenu> userMenus = userMenuDao.findByUserId(user.getId());
        Set<Integer> ids = userMenus.stream().map(userMenu -> userMenu.getMenuId()).collect(Collectors.toSet());
        List<Menu> menus = menuDAO.findAllById(ids);
        List<Menu> res = new ArrayList<>();
        HashMap<Integer, Menu> map = new HashMap<>();

        for (Menu menu : menus) {
            map.put(menu.getId(), menu);
            if (menu.getPid() == null) {
                res.add(menu);
            }
        }

        for (Menu menu : menus) {
            if (menu.getPid() == null) {
                continue;
            }
            Menu p = map.get(menu.getPid());
            List<Menu> children = p.getChildren();
            if (children == null) {
                children = new ArrayList<>();
                p.setChildren(children);
            }
            children.add(menu);
        }
        return res;
    }

    @Transactional
    public void updateUserNenu(Integer userId, Integer[] menuIds) {
        List<UserMenu> userMenus = userMenuDao.findByUserId(userId);
        userMenuDao.deleteInBatch(userMenus);
        for (Integer menuId : menuIds) {
            UserMenu userMenu = new UserMenu(userId, menuId);
            userMenuDao.save(userMenu);
        }
    }
}
