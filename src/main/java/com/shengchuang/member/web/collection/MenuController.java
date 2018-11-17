package com.shengchuang.member.web.collection;

import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.StringUtil;
import com.shengchuang.member.core.domain.Menu;
import com.shengchuang.member.core.repository.UserMenuRepository;
import com.shengchuang.member.core.service.MenuService;
import com.shengchuang.base.AbstractController;
import com.shengchuang.member.web.vo.Ztree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class MenuController extends AbstractController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private UserMenuRepository userMenuRepository;

    @RequestMapping("/admin/rendermenu")
    public List<Menu> rendermenu() {
        return menuService.menus(getSessionAdmin());
    }

    @RequestMapping("/admin/admin/menu/ztree")
    public JsonVO editUserMenu(Integer id) {
        List<Menu> all = menuService.findAll();
        Set<Integer> menuIds = userMenuRepository.findByUserId(id).stream()
                .map(userMenu -> userMenu.getMenuId())
                .collect(Collectors.toSet());
        List<Ztree> ztrees = all.stream().map(menu -> {
            Ztree ztree = new Ztree();
            ztree.setId(StringUtil.toString(menu.getId()));
            ztree.setPId(StringUtil.toString(menu.getPid()));
            ztree.setName(menu.getTitle());
            ztree.setOpen(true);
            if (menuIds.contains(menu.getId())) {
                ztree.setChecked(true);
            }
            return ztree;
        }).collect(Collectors.toList());
        return new JsonVO(ztrees);
    }

    @RequestMapping("/admin/user-menu/update")
    public View updateUserNenu(Integer userId, Integer[] menuIds) {
        menuService.updateUserNenu(userId, menuIds);

        return null;
    }


}
