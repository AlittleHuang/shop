package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.MiningMachine;
import com.shengchuang.member.additional.domain.Ore;
import com.shengchuang.member.additional.service.setting.BonusSettingsService;
import com.shengchuang.member.additional.service.setting.domain.BonusSettings;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.BeanUtil;
import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.member.core.service.UserTreeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.shengchuang.member.core.domain.enmus.BalanceType.INV;
import static com.shengchuang.member.core.domain.enmus.BalanceType.MMD;
import static com.shengchuang.member.core.domain.enmus.Event.*;

/**
 * 实现静态奖及动态奖的结算
 */
@Log4j2
@Service
@Transactional
public class SettlementService {

    @Autowired
    private UserService userService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private UserTreeService userTreeService;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private MiningMachineService miningMachineService;
    @Autowired
    private OreService oreService;
    @Autowired
    private BonusSettingsService bonusSettingsService;

    public void timingSettlement(Type type) {
        if (type == Type.DAILY) {
            dailyIncome();
        }
    }

    /**
     * 每日收益
     */
    public void dailyIncome() {
        BonusSettings s = bonusSettingsService.getSettings();
        List<MiningMachine> machines = miningMachineService.createCriteria().andEqual("status", 0)
                .andGe("endTime", new Date())
                .getList();

        for (MiningMachine machine : machines) {
            double amount = machine.getAmount() * s.miningRate[machine.getLevel()];
            oreService.save(new Ore(amount, machine.getUserId()));
        }
    }

    /**
     * 挖矿
     *
     * @param oreId
     * @param index
     */
    public void pickOreSettlement(int oreId, int index) {

        Ore ore = oreService.getOne(oreId);
        Integer userId = ore.getUserId();
        User user = userService.getOne(userId);

        @SuppressWarnings("ConstantConditions") final double pickAmount =
                (Double) BeanUtil.getBeanProperties(ore, "ore" + index);
        Assert.state(pickAmount > 0, "已收取");
        BeanUtil.setBeanProperties(ore, "ore" + index, 0d);
        oreService.save(ore);

        Balance balance = balanceService.findOrCreateByUserIdAndType(ore.getUserId(), MMD);
        balanceService.incomeAndLog(balance, pickAmount, MINING, "挖矿产值" + pickAmount);

        if (!ore.pickedAll()) {
            return;
        }

        management(user, ore.getTotal());

    }

    /**
     * 注册
     *
     * @param user
     */
    public User register(User user) {
        BonusSettings s = bonusSettingsService.getSettings();
        Integer miningType = user.getMiningType();
        user = userService.register(user);
        Assert.state(miningType > 0 && miningType < s.miningTypeCost.length, "");

        Balance pBalance = balanceService.findOrCreateByUserIdAndType(user.getReferrerId(), MMD);
        String info = "新增矿机" + user.getUsername() + "为M" + miningType;
        balanceService.expenditureAndLog(pBalance, s.miningTypeCost[miningType], ADD_MINING, info);

        invest(user, INVEST_TYPE_REGISTER);
        return user;
    }

    public void updateMining(User user, int miningType) {
        BonusSettings s = bonusSettingsService.getSettings();
        Assert.state(miningType > 0 && miningType < s.miningTypeCost.length, "");
        user.setMiningType(miningType);
        Balance dmm = balanceService.findOrCreateByUserIdAndType(user.getId(), MMD);
        String info = "升级矿机" + user.getUsername() + "为M" + miningType;

        balanceService.expenditureAndLog(dmm, s.miningTypeCost[miningType], ADD_MINING, info);
        invest(user, INVEST_TYPE_UPDATE);
    }

    public static final int INVEST_TYPE_REGISTER = 0;//注册
    public static final int INVEST_TYPE_UPDATE = 1;//升级
    public static final String[] SUB_MEG = {"新增", "升级"};

    /**
     * 矿场主奖励
     *
     * @param user       投资用户
     * @param investType 类型 0->注册, 1->升级
     */
    private void invest(User user, int investType) {
        BonusSettings s = bonusSettingsService.getSettings();
        String typeName = SUB_MEG[investType];

        Balance inv = balanceService.findOrCreateByUserIdAndType(user.getId(), INV);
        Integer miningType = user.getMiningType();
        int cost = s.miningTypeCost[miningType];

        balanceService.incomeAndLog(inv, cost, ADD_MINING, typeName);

        for (MiningMachine machine : miningMachineService.createCriteria()
                .andEqual("userId", user.getId())
                .andEqual("status", 0)
                .getList()) {
            machine.setStatus(1);
            miningMachineService.save(machine);
        }
        miningMachineService.save(new MiningMachine(user.getId(), miningType, cost, s.miningDuration));

        String info = user.getUsername() + typeName + "矿机" + "为:M" + miningType;
        userService.save(user);
        double[] rate = new double[s.kcRate.length];
        for (int i = 0; i < s.kcRate.length; i++) {
            rate[i] = s.kcRate[i] * (1 - s.kcFees);
        }
        List<User> users = loadUpUsersLevel(user);

        double deduction = 0;
        User up = users.get(0);
        if (up.getLevel() == 0) {//分享奖
            deduction = cost * s.ztRate * (1 - s.kcFees);
            Balance balance = balanceService.findOrCreateByUserIdAndType(up.getId(), MMD);
            balanceService.incomeAndLog(balance, deduction, FX, info);
        }
        settlementLevelDiff(users, cost, rate, info, KCZ, deduction);
    }

    /**
     * 社区管理奖励
     *
     * @param user   收益会员
     * @param amount 收益数量
     */
    private void management(User user, double amount) {
        BonusSettings s = bonusSettingsService.getSettings();

        String info = user.getUsername() + "获得收益" + NumberUtil.toString(amount, 4, RoundingMode.HALF_UP);

        settlementLevelDiff(loadUpUsersLevel(user), amount, s.sqRate, info, SQGL, 0);
    }

    /**
     * 计算极差
     */
    private void settlementLevelDiff(List<User> users, double amount, double[] rate, String info, Event event, double deduction) {
        double totalBonus = deduction;//极差
        for (User u : users) {
            double bonus = rate[u.getLevel()] * amount;
            double bonusAdd = bonus - totalBonus;
            if (bonusAdd > 0) {
                totalBonus = bonus;
                Balance balance = balanceService.findOrCreateByUserIdAndType(u.getId(), MMD);
                balanceService.incomeAndLog(balance, bonusAdd, event, info);
            }
        }
    }

    private List<User> loadUpUsersLevel(User user) {
        List<Integer> pids = userTreeService.getPids(user.getId());
        List<User> users = userService.findAllById(pids);
        userLevelService.loadUscLevel(users);
        Map<Integer, User> userMap = User.mapById(users);
        return pids.stream().map(userMap::get).collect(Collectors.toList());
    }

    public enum Type {
        DAILY, MONTH
    }

}
