package com.shengchuang.member.additional.service;

import com.shengchuang.common.util.StreamUtil;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.util.BalancesIndex;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.member.core.service.UserTreeService;
import com.shengchuang.member.core.utils.TimeCache;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.shengchuang.member.core.domain.enmus.BalanceType.INV;

@CommonsLog
@Service
public class UserLevelService implements StreamUtil {

    public static final int INV_LIMIT = 100000;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private UserTreeService userTreeService;
    @Autowired
    private UserService userService;

    /**
     * 获取等级
     *
     * @param user 单个user
     */
    public void loadUscLevel(User user) {
        loadUscLevel(Collections.singletonList(user));
    }

    /**
     * 批量获取等级
     *
     * @param users user集合
     */
    public void loadUscLevel(Collection<User> users) {
        loadUscLevel(users, null);
    }

    // 直推一个会员为一个市场,投资为激活或升级时消耗的DMM数量
    // V1: 团队投资总和累计不小于100000
    // V2：团队中有1个市场有V1,且其它市场投资总和不小于100000
    // V3：团队中有2个市场有V1,且其它市场投资总和不小于100000
    // V4: 社区下至少3个市场,且这3个市场其中2个有V1,1个有V3
    // V5: 社区下至少3个市场,且这3个市场其中1个有V1,2个有V3
    // V6: 社区下至少3个市场,且这3个市场都有V1
    // V7: 社区下至少3个市场,且这3个市场其中2个有V3,1个有V6
    // V8: 社区下至少3个市场,且这3个市场其中1个有V3,2个有V6
    // V9：社区下至少3个市场,且这3个市场都有V6

    public void loadUscLevel(Collection<User> users, BalancesIndex balances) {
        if (balances == null) {
            Collection<Integer> teamIds = userTreeService.getTeamIds(User.idList(users));
            balances = balanceService.getIndex(teamIds, INV);
        }
        Map<Integer, Object> map = new HashMap<>(); // 保存临时数据 Map<Integer, UData>
        BalancesIndex index = balances;

        class UData {
            final int userId;
            final double inv; // 自己的投资数量
            Double teamInv; // 团队投资数量

            public Double getTeamInv() {
                if (teamInv == null) {
                    teamInv = 0d;
                    for (UData child : getChildren()) {
                        teamInv += (child.getTeamInv() + child.inv);
                    }
                }
                return teamInv;
            }

            List<UData> children; // 直推成员

            public List<UData> getChildren() {
                if (children == null) {
                    children = new ArrayList<>();
                    Collection<Integer> childrenIds = userTreeService.getChildrenIds(userId);
                    for (Integer childrenId : childrenIds) {
                        children.add((UData) map.computeIfAbsent(childrenId, userId -> new UData(userId)));
                    }
                }
                return children;
            }

            int[] teamLvCount;//团队中 LV 0-9 的市场数量

            public int[] getTeamLvCount() {
                if (teamLvCount == null) {
                    teamLvCount = new int[10];
                    for (UData child : getChildren()) {
                        Integer lv = child.getLv();
                        for (int i = 0; i < teamLvCount.length; i++) {
                            if (lv >= i || child.getTeamLvCount()[i] > 0) {
                                teamLvCount[i]++;
                            }
                        }
                    }
                }
                return teamLvCount;
            }

            Integer lv; // 等级

            public UData(int userId) {
                this.userId = userId;
                this.inv = index.getAmount(userId, INV);
                map.put(userId, this);
            }

            public Integer getLv() {
                if (lv == null) {
                    Double teamInv = getTeamInv();
                    if (teamInv < INV_LIMIT) {
                        lv = 0;
                        return lv;
                    } else if (teamInv < 2 * INV_LIMIT || getChildren().size() == 1) {
                        lv = 1;
                        return lv;
                    }
                    int[] teamLvCount = getTeamLvCount();

                    int v6 = teamLvCount[6] + teamLvCount[7] + teamLvCount[8] + teamLvCount[9];
                    int v3 = teamLvCount[3] + teamLvCount[4] + teamLvCount[5] + v6;
                    int v1 = teamLvCount[1] + teamLvCount[2] + v3;

                    if (v6 >= 3) {// 至少三个V6
                        lv = 9;
                    } else if (v6 >= 2 && v3 >= 3) { // 至少1个v3，2个V6
                        lv = 8;
                    } else if (v6 >= 1 && v3 >= 3) { // 至少2个v3，1个V6.
                        lv = 7;
                    } else if (v3 >= 3) {            // 至少3个V3
                        lv = 6;
                    } else if (v3 >= 2 && v1 >= 3) { // 至少1个V1、2个V3
                        lv = 5;
                    } else if (v3 >= 1 && v1 >= 3) { // 2个V1、1个V3
                        lv = 4;
                    } else {
                        double otherInv = 0; // 没有等级的直推团队总和
                        for (UData child : getChildren()) {
                            if (child.getLv() == 0) {
                                otherInv += (child.getTeamInv() + child.inv);
                            }
                        }

                        if (v1 >= 3 || (v1 == 2 && otherInv >= INV_LIMIT)) {        // 直推2个V1、且其它市场投资总和达100000INV
                            lv = 3;
                        } else if (v1 == 2 || (v1 == 1 && otherInv >= INV_LIMIT)) { // 直推1个V1、且其它市场投资总和达100000INV
                            lv = 2;
                        } else if (teamInv >= INV_LIMIT) {             // 团队INV累计达100000INV
                            lv = 1;
                        }

                    }
                }
                return lv;
            }
        }

        for (User user : users) {
            //noinspection Convert2MethodRef
            UData data = (UData) map.computeIfAbsent(user.getId(), userId -> new UData(userId));
            Integer agent = user.getAgent() == null ? User.AGENT_ZD : user.getAgent();
            if (agent != null && agent == User.AGENT_SD) {
                Integer sdLevel = user.getAdminLevel() == null ? 0 : user.getAdminLevel();
                Integer zdLevel = data.getLv();
                user.setLevel(Math.max(sdLevel, zdLevel));
            } else {
                user.setLevel(data.getLv());
            }
        }

    }


    private final TimeCache<long[]> levelInfo = new TimeCache<>(5 * TimeUtil.MILLIS_PER_MINUTE, () -> {
        List<User> allUsers = userService.findAll();
        loadUscLevel(allUsers);
        userService.saveAll(allUsers);
        long[] counts = new long[10];
        for (User user : allUsers) {
            counts[user.getLevel()]++;
        }
        return counts;
    });

    /**
     * 统计总的矿机等级数
     */
    private final TimeCache<CountMiningAndLevel> countMiningAndLevelInfo = new TimeCache<>(5 * TimeUtil.MILLIS_PER_MINUTE, () -> {
        List<User> allUsers = userService.findAll();
        loadUscLevel(allUsers);
        userService.saveAll(allUsers);
        long[] levelCounts = new long[10];
        long[] miningCounts = new long[4];
        long[] newMiningCounts = new long[4];
        Date date = new Date();
        for (User user : allUsers) {
            levelCounts[user.getLevel()]++;
            miningCounts[user.getMiningType() - 1]++;
            Date registTime = user.getRegistTime();
            if (registTime.getTime() > TimeUtil.getStartTimeOfDate(date).getTime() && registTime.getTime() < TimeUtil.getOverTimeOfDate(date).getTime()) {
                newMiningCounts[user.getMiningType() - 1]++;
            }
        }
        CountMiningAndLevel countMiningAndLevel = new CountMiningAndLevel();
        countMiningAndLevel.levelCounts = levelCounts;
        countMiningAndLevel.miningCounts = miningCounts;
        countMiningAndLevel.newMiningCounts = newMiningCounts;
        return countMiningAndLevel;
    });
    @Data
    @NoArgsConstructor
    public static class CountMiningAndLevel {
        long[] levelCounts;
        long[] miningCounts;
        long[] newMiningCounts;
    }

    @Transactional
    public void updateAllUserLevel() {
        levelInfo.get();
    }

    public CountMiningAndLevel getCountMiningAndLevel() {
        return countMiningAndLevelInfo.get();
    }

}
