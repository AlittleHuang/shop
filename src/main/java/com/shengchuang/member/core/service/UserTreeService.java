package com.shengchuang.member.core.service;

import com.shengchuang.common.util.StreamUtil;
import com.shengchuang.common.util.Tree;
import com.shengchuang.member.core.domain.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class
UserTreeService extends AbstractUSerTreeService<Tree.IdNode<Integer>, UserTreeService.UserIdTree> implements
        StreamUtil {

    {
        User.removeCallbackList.add(user -> {
            if (tree != null) {
                tree.remove(user.getId());
            }
        });
        User.insertCallbackList.add(user -> {
            if (tree != null) {
                tree.add(new Tree.IdNode<>(user.getId(), user.getReferrerId()));
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void reset() {
        List<Object[]> list = (List<Object[]>) createCriteria().addSelect("id", "referrerId").getObjList();
        List<Tree.IdNode<Integer>> nodes = toList(list, ids -> new Tree.IdNode(ids[0], ids[1]));
        tree = new UserIdTree(nodes);
    }

    @Override
    protected int getOldSize() {
        return tree == null ? -1 : tree.size();
    }


    public static class UserIdTree extends Tree<Integer, Integer, Tree.IdNode<Integer>> {
        public UserIdTree(@NotNull Collection<? extends IdNode<Integer>> nodes) {
            super(nodes);
        }
    }


}
