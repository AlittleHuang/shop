package com.shengchuang.member.core.utils;

import com.shengchuang.common.util.Tree;
import com.shengchuang.member.core.domain.User;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserTrees<N extends Tree.Node<User, Integer>> {

    @Delegate
    private final Tree<User, Integer, N> tree;

    public UserTrees(Collection<N> nodes) {
        tree = new Tree<>(nodes);
    }

    public static class Node implements Tree.Node<User, Integer> {

        public final User user;
        private final Set<Integer> childrenIds = new HashSet<>();

        public Node(User user) {
            this.user = user;
        }

        @Override
        public Integer getId() {
            return user.getId();
        }

        @Nullable
        @Override
        public Integer getPid() {
            return user.getReferrerId();
        }

        @NotNull
        @Override
        public Set<Integer> getChildrenIds() {
            return childrenIds;
        }

        @Override
        public User data() {
            return user;
        }
    }

}