package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.Bonus;
import com.shengchuang.member.additional.service.setting.domain.BonusSettings;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.domain.util.BalancesIndex;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

@Service
public class BonusService extends AbstractService<Bonus, Integer> {

    @Autowired
    private UserService userService;
    @Autowired
    private BalanceService balanceService;

    /**
     * 加载BalanceLog.user
     *
     * @param logs
     * @return
     */
    public <T extends Collection<Bonus>> T loadUsername(T logs) {
        userService.loadUser(logs, (log) -> log.getUserId(),
                (log, user) -> log.setUser(user == null ? null : new User(user.getUsername())));
        return logs;
    }


    /**
     * 分页查询
     *
     * @param user           查询用户,null则查询所有
     * @param pageRequestMap 前段传的查询条件
     * @return
     */
    public Page<Bonus> getPage(User user, PageRequestMap pageRequestMap) {
        Criteria<Bonus> conditions = toPageConditions(pageRequestMap);
        conditions.addOrderByDesc("time");
        Criteria<Bonus> criteria = conditions;

        addTimeFilter(criteria, pageRequestMap, "time");

        Date date = pageRequestMap.getDateValue("date");
        if (date != null) {
            criteria.andBetween("time", TimeUtil.getStartTimeOfDate(date), TimeUtil.getOverTimeOfDate(date));
        }
        criteria.andNotBetween("amount", -Balance.MIN_CHANGE, Balance.MIN_CHANGE);
        return getPage(conditions);
    }


    @Transactional
    public void add(int userId, double amount, Event event, String info,
                    BalancesIndex balances, BonusSettings settings) {

        toBalances(userId, amount, event, info, balances, settings);

        save(new Bonus(userId, amount, event, Bonus.STATUS_YES, info));
    }

    public void toBalances(int userId,
                           double amount,
                           Event event,
                           String info,
                           BalancesIndex balances,
                           BonusSettings settings) {
//        add(userId, amount, event, info, balances, BalanceType.MMD, settings.getBonus2Sl());
    }

}
