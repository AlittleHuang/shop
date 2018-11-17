package com.shengchuang.member.core.service;

import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.common.util.ObjectUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.domain.util.BalancesIndex;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BalanceService extends BaseBalanceService {

    /**
     * 根据UserId,Type获取Balance,不存在则创建一个
     *
     * @param userId
     * @param type
     * @return
     */
    @Transactional
    public Balance findOrCreateByUserIdAndType(int userId, int type) {
        return findOrCreateByUserIdAndType(userId, BalanceType.of(type));
    }

    /**
     * 根据UserId,Type获取Balance,不存在则创建一个
     *
     * @param userId
     * @param type
     * @return
     */
    @Transactional
    public Balance findOrCreateByUserIdAndType(int userId, BalanceType type) {
        Assert.isTrue(type != null, "币种类型错误");
        Balance balance = findByUserIdAndType(userId, type);
        if (balance == null) {
            balance = insert(userId, type, true);
        }
        return balance;
    }

    @Transactional
    public Balance insert(int userId, BalanceType type, boolean check) {
        Balance balance = findByUserIdAndType(userId, type);
        if (balance == null) {
            Assert.state(!check || userService.existsById(userId), "创建币种账户错误:用户不存在");
            balance = new Balance(type, userId);
            balance = saveAndFlush(balance);
        }
        return balance;
    }

    /**
     * 加载Balance.user
     *
     * @param balance
     */
    public void loadUser(Balance balance) {
        User user = userService.getOne(balance.getUserId());
        balance.setUser(user);
    }

    public void loadUsers(Collection<Balance> balances) {
        userService.loadUser(balances, balance -> balance.getUserId(), (balance, user) -> balance.setUser(user));
    }

    public List<Balance> findByTypeUserIdIn(int type, Collection<Integer> ids) {
        return balanceDao.findByTypeAndUserIdIn(type, ids);
    }

    public List<Balance> findByTypeInAndUserIdIn(Collection<Integer> types, Collection<Integer> ids) {
        return balanceDao.findByTypeInAndUserIdIn(types, ids);
    }

    /**
     * 按条件求和
     *
     * @param conditions 筛选条件
     * @return
     */
    public double sumAmountOfBalance(Criteria<Balance> conditions) {
        conditions.addSelectSum("amount");
        return ObjectUtil.getNonNull((Double) userService.findOneObj(conditions), 0.0);
    }

    public double sumAmountOfBalance(Map<String, ?> conditions) {
        Double amount = (Double) createCriteria().andEqual(conditions).addSelectSum("amount").getOneObject();
        return amount == null ? 0d : amount;
    }

    /**
     * 按类型求和
     *
     * @param type
     * @return
     */
    public double sumAmountOfBalance(int type) {
        Criteria<Balance> conditions = createCriteria().andEqual("type", type);
        return NumberUtil.halfUp(sumAmountOfBalance(conditions), 2);
    }

    /* * * * * * * * * * * * * CRUD * * * * * * * * * * * */

    public Map<String, Double> getBalancesMap(int userId, Integer... types) {
        User user = userService.getOne(userId);
        Assert.state(user != null, "id错误");
        Assert.state(user.getStatus() > 0, "未激活的用户");
        Map<String, Double> balances = new HashMap<>();
        createCriteria().andEqual("userId", userId).andIn("type", types).getList().forEach(balance -> {
            balances.put("balance_" + balance.getType(), balance.getAmount());
        });
        return balances;
    }

    public double[] getBalances(int userId) {
        // User user = userService.getOne(userId);
        // Assert.state(user != null, "id错误");
        // Assert.state(user.getLevel() > 0, "未激活的用户");
        int count = BalanceType.values().length;
        double[] balances = new double[count];
        findByUserIdAndTypeBetween(userId, 0, count - 1)
                .forEach(balance -> balances[balance.getType()] = balance.getAmount());
        return balances;
    }

    private List<Balance> findByUserIdAndTypeBetween(int userId, int minType, int maxType) {
        return balanceDao.findByUserIdAndTypeBetween(userId, minType, maxType);
    }

    /**
     * 用 {@link BalanceService#findOrCreateByUserIdAndType} 替代
     */
    @Deprecated
    public Balance findByUserIdAndType(int userId, BalanceType type) {
        return balanceDao.findByUserIdAndType(userId, type.index);
    }

    public List<Balance> findByUserIdInAndType(Collection<Integer> userIds, int type) {
        return createCriteria().andIn("userId", userIds).andEqual("type", type).getList();
    }

    public Page<Balance> getPage(PageRequestMap pageRequestMap) {
        Criteria<Balance> conditions = toPageConditions(pageRequestMap);
        conditions.and().andGt("amount", 0);// .andEqual("type",
        // Balance.TYPE_2);
        userService.addFindByUsername(conditions, pageRequestMap);
        return getPage(conditions);
    }

    public void clean(int userId, BalanceType type) {
        Assert.state(userService.existsById(userId), "ID错误");
        Balance balance = findOrCreateByUserIdAndType(userId, type);
        expenditureAndLog(balance, balance.getAmount(), Event.RECHARGE_BY_ADMIN, "系统清空");
    }


    public BalancesIndex getIndex(Collection<Integer> userIds, BalanceType... types) {
        return getIndex(userIds, Arrays.asList(types));
    }

    public BalancesIndex getIndex(Collection<Integer> userIds, List<BalanceType> types) {
        List<Balance> balances = balanceDao.findByUserIdInAndTypeIn(
                new ArrayList<>(userIds),
                types.stream().map(BalanceType::getIndex).collect(Collectors.toList())
        );
        return new BalancesIndex(balances);
    }

    @Transactional
    public void initUsersBalance(Integer id) {
        for (BalanceType type : BalanceType.values()) {
            insert(id, type, false);
        }
    }
}
