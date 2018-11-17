package com.shengchuang.member.core.service;

import com.shengchuang.common.util.StreamUtil;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.common.util.Tree;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.service.domain.UserTrees;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class
AbstractUSerTreeService<NODE extends Tree.IdNode<Integer>, TREE extends Tree<Integer, Integer, NODE>>
        extends BaseUserService
        implements StreamUtil {

    protected TREE tree;

    protected long checkCountTime = 0L;
    protected long interval = TimeUtil.MILLIS_PER_SECOND * 10;

    protected boolean countChecked() {
        if (System.currentTimeMillis() - checkCountTime < interval)
            return true;

        synchronized (this) {
            if (System.currentTimeMillis() - checkCountTime < interval)
                return true;
            boolean res = getOldSize() == count();
            checkCountTime = System.currentTimeMillis();
            return res;
        }
    }

    protected abstract int getOldSize();

    public abstract void reset();

    protected TREE getUserTree() {
        if (tree == null || !countChecked()) {
            reset();
        }
        return tree;
    }

    public Collection<Integer> getAllUserIds() {
        return getUserTree().ids();
    }

    public List<Integer> getPids(int userId) {
        return getUserTree().pids(userId);
    }

    public Collection<Integer> getChildrenIds(int userId) {
        return getUserTree().getNode(userId).getChildrenIds();
    }

    public List<Integer> getTeamIds(int userId, boolean containSelf, int depth) {
        return getUserTree().subIds(userId, depth, containSelf);
    }

    /**
     * userIds 的下级 id
     *
     * @param userIds
     * @param containSelf
     * @param depth
     * @return
     */
    public List<Integer> getTeamIds(Collection<Integer> userIds, boolean containSelf, int depth) {
        return getUserTree().subIds(userIds, depth, containSelf);
    }

    /**
     * 获取团队id集合
     *
     * @param userId
     * @param containSelf 是否包含自己
     * @return
     */
    public List<Integer> getTeamIds(int userId, boolean containSelf) {
        return getTeamIds(userId, containSelf, Integer.MAX_VALUE);
    }

    /**
     * 获取团队多个团队id集合
     *
     * @param userIds
     * @return
     */
    public List<Integer> getTeamIds(Collection<Integer> userIds) {
        return getTeamIds(userIds, true, Integer.MAX_VALUE);
    }

    /**
     * 获取团队id集合
     *
     * @param userId      userId
     * @param containSelf 包含自己
     * @param depth       层数
     * @return
     */
    public List<User> getTeamUsers(Integer userId, boolean containSelf, int depth) {
        return findAllById(getTeamIds(Collections.singletonList(userId), containSelf, depth));
    }

    public boolean ofTeam(User root, User node) {
        return ofTeam(root.getId(), node.getId());
    }

    /**
     * 判断是否团队关系
     *
     * @param rootId
     * @param nodeId
     * @return
     */
    public boolean ofTeam(int rootId, int nodeId) {
        Tree<Integer, Integer, NODE> userTree = getUserTree();
        NODE node = userTree.getNode(nodeId);
        if (node == null)
            return false;
        NODE root = userTree.getNode(rootId);
        if (root == null)
            return false;
        if (rootId == nodeId) return true;
        int x = 0;
        while (x++ <= userTree.size() && (node = userTree.getNode(node.getPid())) != null) {
            if (rootId == node.getId())
                return true;
        }
        return false;
    }


    /**
     * 判断是否直系关系
     *
     * @param one
     * @param other
     * @return
     */
    public boolean lineal(int one, int other) {
        if (other > one) {
            int temp = other;
            other = one;
            one = temp;
        }
        return ofTeam(one, other) || ofTeam(other, one);
    }

    private UserTrees<UserTrees.NodeImpl> getUserTrees(int userId) {
        return getUserTrees(Collections.singletonList(userId));
    }

    private UserTrees<UserTrees.NodeImpl> getUserTrees(Collection<Integer> userIds) {
        List<Integer> teamIds = getTeamIds(userIds);
        return new UserTrees<>(findAllById(teamIds), UserTrees.NodeImpl::new);
    }

}
