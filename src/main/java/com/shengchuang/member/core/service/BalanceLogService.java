package com.shengchuang.member.core.service;

import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.BalanceLog;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class BalanceLogService extends AbstractService<BalanceLog, Integer> {

    @Autowired
    private UserService userService;


    @Override
    public Criteria<BalanceLog> createCriteria() {
        return super.createCriteria().andNotEqual("type", BalanceType.INV.index);
    }

    /**
     * 分页查询
     *
     * @param user           查询用户,null则查询所有
     * @param pageRequestMap 前段传的查询条件
     * @return
     */
    public Page<BalanceLog> getPage(User user, PageRequestMap pageRequestMap) {
        Criteria<BalanceLog> conditions = toPageConditions(pageRequestMap);
        conditions.addOrderByDesc("time", "operation", "currentBalance");
        Criteria<BalanceLog> criteria = conditions;
        String typeIn = pageRequestMap.get("typeIn");
        if (typeIn != null && !"".equals(typeIn)) {
            String[] type = typeIn.split(",");
            List<Integer> list = toList(Arrays.asList(type), Integer::valueOf);
            criteria.andIn("type", list);
        }

        Double amountMin = pageRequestMap.getDoubleValue("amountMin");
        if (amountMin != null) {
            criteria.andGe("amount", amountMin);
        }
        Double amountMax = pageRequestMap.getDoubleValue("amountMax");
        if (amountMin != null) {
            criteria.andLe("amount", amountMax);
        }
        userService.addUserFilter(user, pageRequestMap, criteria);

        addTimeFilter(criteria, pageRequestMap, "time");

        Date date = pageRequestMap.getDateValue("date");
        if (date != null) {
            criteria.andBetween("time", TimeUtil.getStartTimeOfDate(date), TimeUtil.getOverTimeOfDate(date));
        }

        String statisticType = pageRequestMap.get("statisticType");//统计类型
        String in = pageRequestMap.get("in");
        String out = pageRequestMap.get("out");
        if (in != null) {
            criteria.andGt("amount", 0);
        }
        if (out != null) {
            criteria.andLt("amount", 0);
        }
        if ("investment".equals(statisticType)) { // 投资统计

        } else if ("bonus".equals(statisticType)) { // 奖金统计
            System.out.println(BalanceLog.bonusOpt());
            criteria.andIn("operation", BalanceLog.bonusOpt());//, BalanceLog.SERVER
        }
        criteria.andNotBetween("amount", -Balance.MIN_CHANGE, Balance.MIN_CHANGE);
        return getPage(conditions);
    }

    /**
     * 加载BalanceLog.user
     *
     * @param logs
     * @return
     */
    public <T extends Collection<BalanceLog>> T loadUsername(T logs) {
        new BalanceLog();
        userService.loadUser(logs, (log) -> log.getUserId(), (log, user) -> log.setUser(user));
        return logs;
    }

    public double findSumAmount(int userId, Event event) {
        Criteria<BalanceLog> conditions = createCriteria();
        conditions
                .andEqIgnoreEmpty("userId", userId)
                .andEqIgnoreEmpty("operation", event.getIndex());
        return sum(conditions);
    }

    public double sum(Criteria<BalanceLog> criteria) {
        Object res = criteria.addSelectSum("amount").getOneObject();
        return (double) (res == null ? 0.0 : res);
    }

    public Long countByOperationToday(int operation, int userId) {
        Criteria<BalanceLog> conditions = createCriteria();
        Date date = new Date();
        conditions.andEqIgnoreEmpty("userId", userId)
                .andEqIgnoreEmpty("operation", operation)
                .andBetween("time", TimeUtil.getStartTimeOfDate(date), TimeUtil.getOverTimeOfDate(date));
        long count = conditions.count();
        return count;
    }
}
