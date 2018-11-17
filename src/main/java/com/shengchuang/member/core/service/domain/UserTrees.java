package com.shengchuang.member.core.service.domain;

import com.shengchuang.common.util.Tree;
import com.shengchuang.member.core.domain.User;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserTrees<T extends Tree.Node<T, Integer>> extends Tree<T, Integer, T> {

    public UserTrees(Collection<User> users, Function<User, T> asNode) {
        super(users.stream().map(asNode).collect(Collectors.toList()));
    }

    public static class NodeImpl implements Tree.Node<NodeImpl, Integer> {

        @Delegate
        protected final User user;
        @Getter
        private Set<Integer> childrenIds;

        public NodeImpl(User user) {
            this.user = user;
        }

        @Override
        public NodeImpl data() {
            return this;
        }

        @Nullable
        @Override
        public Integer getPid() {
            return user.getReferrerId();
        }
    }

}