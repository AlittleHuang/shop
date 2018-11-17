package com.shengchuang.member.web.collection;

import com.shengchuang.member.additional.service.UserLevelService;
import com.shengchuang.member.additional.service.setting.BonusSettingsService;
import com.shengchuang.member.additional.service.setting.domain.BonusSettings;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.mvc.view.JsonView;
import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.domain.util.BalancesIndex;
import com.shengchuang.member.core.utils.UserTrees;
import com.shengchuang.base.AbstractController;
import com.shengchuang.member.web.vo.LayTreeNode;
import com.shengchuang.member.web.vo.Ztree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserDataController extends AbstractController {

    @Autowired
    BonusSettingsService bonusSettingsService;

    /**
     * 获取当前用户信息
     *
     * @return
     */
    @RequestMapping("/front/user")
    public View user() {
        User user = userService.getOne(getSessionUser().getId());
        userService.loadPusername(user);
        return new JsonVO(user);
    }

    @RequestMapping("/front/user/team/count")
    public View teamCount() {
        return new JsonMap().add("count", userTreeService.getTeamIds(getSessionUser().getId(), false).size());
    }

    /**
     * 获取单签用个信息,包括详细信息
     *
     * @return
     */
    @RequestMapping("/front/user/info")
    public View userInfo() {
        User user = userService.getOne(getSessionUser().getId());
        userService.loadUserInfo(user);
        return new JsonVO(user);
    }

    /**
     * 用户资料是否完整
     *
     * @return
     */
    @RequestMapping("/front/user/info/complete")
    public View userInfoComplete() {
        User user = userService.getOne(getSessionUser().getId());
        boolean flag = false;
        if (user.getPhone() != null && user.getIdCard() != null && user.getActualName() != null) {
            flag = true;
        }
        return new JsonVO(flag);
    }


    /**
     * 会员列表
     *
     * @return
     */
    @RequestMapping("/admin/user/list")
    public JsonView userList() {
        if (StringUtils.hasText(request().getParameter("level"))) {
            userLevelService.updateAllUserLevel();
        }
        Page<User> page = userService.getPage(getPageRequestMap());

        userService.loadPusername(page.getContent());
        userLevelService.loadUscLevel(page.getContent());

        return new JsonVO(page);
    }
    /**
     * 会员列表
     *
     * @return
     */
    @RequestMapping("/admin/user/level/count")
    public JsonView levelCount() {
        /*long[] levelCounts = userLevelService.getLevelCount();
        long[] miningCount = userLevelService.getMiningCount();
        long[] newMiningCount = userLevelService.getNewMiningCount();*/
        UserLevelService.CountMiningAndLevel countMiningAndLevel = userLevelService.getCountMiningAndLevel();
        return new JsonMap().add("levelCounts",countMiningAndLevel.getLevelCounts())
                .add("miningCounts",countMiningAndLevel.getMiningCounts())
                .add("newMiningCounts",countMiningAndLevel.getNewMiningCounts());
    }

    /**
     * 后台推荐图
     *
     * @return
     */
    @RequestMapping("/admin/user/tree")
    public List<LayTreeNode> getLayTreeAdmin() {
        List<User> all = userService.findAll();
        return toTree(all);
    }

    /**
     * 用户推荐图
     *
     * @return
     */
    @RequestMapping("/front/user/tree")
    public List<LayTreeNode> getLayTreeFront() {
        List<User> list = userService.getTeamList(getSessionUser());
        //list.add(getSessionUser());
        return toTree(list);
    }

    private List<LayTreeNode> toTree(List<User> users) {
        List<LayTreeNode> trees = new ArrayList<>(users.size());
        for (User user : users) {
            trees.add(new LayTreeNode(toString(user.getId()), toString(user.getReferrerId()), user.getUsername()));
        }
        trees = LayTreeNode.organize2Tree(trees);
        return trees;
    }


    @RequestMapping("/fronts/user/ztree")
    public List<Ztree> getZtree() {
        User user = refreshSessionUser();
        List<User> users = new ArrayList<>();
        users.add(user);
        if (userService.getTeamList(user) != null) {
            users.addAll(userService.getTeamList(user));
        }

        List<Integer> userIds = new ArrayList<>();
        for (User u : users) {
            userIds.add(u.getId());
        }
        List<Ztree> ztrees = userService.toZtree(users, userIds);
        return ztrees;
    }

    @RequestMapping("/fronts/user/relationship")
    public View getRelationshipUsers() {
        User user = refreshSessionUser();
        List<User> users = new ArrayList<>();
        if (userService.getTeamList(user) != null) {
            users.addAll(userService.getTeamList(user));
        }
        PageImpl<User> page = new PageImpl<>(users);
        return new JsonVO(page);
    }

    /**
     * 后端直推系谱图
     *
     * @return
     */
    @RequestMapping("/admin/user/ztree")
    public View getAllZtree(User user) {
        Criteria<User> userCriteria = userService.createCriteria().andEqual(user);
        List<User> users = new ArrayList<>();
        long count = userCriteria.count();
        Assert.state(count > 0, "找不到该用户");
        try {
            if (count == 1) {
                user = userCriteria.getOne();
                users.add(user);
                if (userService.getTeamList(user) != null) {
                    users.addAll(userService.getTeamList(user));
                }

            }
        } catch (Exception e) {
        }
        if (users == null || users.size() == 0)
            users = userService.findAll();
        List<Integer> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        List<Ztree> ztree = userService.toZtree(users, userIds);
        return new JsonMap().add(ztree);
    }

    @RequestMapping("/front/user/ztree")
    public View getPcZtree() {
        User user = getSessionUser();
        List<Integer> teamIds = userTreeService.getTeamIds(user.getId(), true);
        List<User> users = userService.findAllById(teamIds);
        BalancesIndex index = balanceService.getIndex(teamIds, BalanceType.MMD);
        UserTrees<Node> trees = new UserTrees<>(toList(users, Node::new));
        List<Ztree> ztrees = toList(trees.nodes(),
                node -> {
                    String name = node.user.getUsername();
                    return new Ztree(node.getId(), node.getPid(), name);
                }
        );
        Ztree root = ztrees.get(0);
        String rootName = root.getName();
        BonusSettings settings = bonusSettingsService.getSettings();

        boolean first = true;
        int count = 0;

        for (Integer teamId : teamIds) {
            if (first) {
                first = false;
            } else {
                count++;
            }
        }

        rootName += " ,节点数: " + count;
        root.setName(rootName);

        return new JsonMap().add("ztreeList", ztrees);
    }

    @RequestMapping("/admin/user/chidren")
    public View getChiled() {
        Integer id = intParameter("id");
        Assert.notNull(id, "缺少参数(int):id");
        List<User> users;
        if (id == -1) {
            users = new ArrayList<>(1);
            User user = userService.getOne(User.ROOT_USER_ID);
            users.add(user);
        } else {
            Assert.notNull(userService.getOne(id), "id错误");
            users = userService.findByReferrerId(id);
        }

        return new JsonVO(users);
    }

    @RequestMapping("/front/user/chidren")
    public View getChiledF() {
        Integer id = intParameter("id");
        Assert.notNull(id, "缺少参数(int):id");
        List<User> users;
        if (id == -1) {
            users = new ArrayList<>(1);
            User user = userService.getOne(getSessionUser().getId());
            users.add(user);
        } else {
            Assert.notNull(userService.getOne(id), "id错误");
            users = userService.findByReferrerId(id);
        }

        return new JsonVO(users);
    }

    @RequestMapping("/admin/user/getOne")
    public View getOne(Integer id) {
        User user = userService.getOne(id);
        if(user.getAgent()==null)user.setAgent(User.AGENT_ZD);
        return new JsonVO(user);
    }

    @RequestMapping("/front/user/getOne")
    public View getOne() {
        Integer id = getSessionUser().getId();
        User user = userService.getOne(id);
        userLevelService.loadUscLevel(user);
        return new JsonVO(user);
    }

    @RequestMapping("/front/user/recome/list")
    public View recomeList() {
        PageRequestMap pageRequestMap = getPageRequestMap();
        pageRequestMap.add("referrerId", getSessionUser().getId());
        Page<User> page = userService.getPage(pageRequestMap);
        return new JsonVO(page);
    }


    class Node extends UserTrees.Node {

        Double teamMl;

        public Node(User user) {
            super(user);
        }

    }

}
