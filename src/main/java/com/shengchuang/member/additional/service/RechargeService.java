package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.Balance;
import com.shengchuang.member.core.domain.Recharge;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.UserService;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RechargeService extends AbstractService<Recharge, Integer> {
    /**
     * 充值汇率
     */
    public static final String RECHARGE_EXCHANGE_RATE = "rechargeExchangeRate";
    /**
     * 充值日期(0到7对应星期日到星期六)
     */
    private static final String RECHARGE_WEEK_DAY = "rechargeWeekDay";
    /**
     * 最低充值
     */
    private static final String RECHARGE_MIN = "rechargeMin";
    /**
     * 最高充值
     */
    private static final String RECHARGE_MAX = "rechargeMax";

    /**
     * 充值手续费
     *//*
    private static final String RECHARGE_FEE = "rechargeFee";*/

//    private static final String ADMIN_RECHARGE_INFO = "系统充值";
    /**
     * 充值倍数
     */
    private static final String RECHARGE_FACTOR = "rechargeFactor";
    @Autowired
    private UserService userService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private SettingsService settingsService;

    public Page<Recharge> listByUserId(int userId, PageRequestMap pageRequestMap) {
        Criteria<Recharge> conditions = toPageConditions(pageRequestMap)
                .addOrderByDesc("createTime").and().andEqual("userId", userId);
        Page<Recharge> page = getPage(conditions, pageRequestMap);
        return page;
    }

    /**
     * @param pageRequestMap
     * @return
     * @Update fw
     */
    public Page<Recharge> listByUserId(PageRequestMap pageRequestMap) {
        Criteria<Recharge> conditions = toPageConditions(pageRequestMap).addOrderByDesc("createTime");
        //用户名查询条件
        String username = pageRequestMap.get("username");
        if (isNotBlank(username)) {
            Integer userId = userService.findUserIdByUsername(username);
            if (userId != null) {
                conditions.andEqual("userId", userId);
            } else {
                conditions.setPageResultEmpty();
            }
        }
        //开始时间查询条件
        Date startTime = pageRequestMap.getDateValue("startTime");
        //结束时间查询条件
        Date endTime = pageRequestMap.getDateValue("endTime");
        if (startTime != null && !"".equals(startTime)) {
            conditions.andGe("createTime", startTime, Date.class);
        }
        if (endTime != null && !"".equals(endTime)) {
            endTime = TimeUtil.addDay(endTime, 1);
            conditions.andLt("createTime", endTime, Date.class);
        }
        //币种条件类型
        if (pageRequestMap.getIntValue("type") != null && !"".equals(pageRequestMap.getIntValue("type"))) {
            int type = pageRequestMap.getIntValue("type");
            conditions.andEqual("balanceType", type);
        }
        //时间查询条件
        Date date = pageRequestMap.getDateValue("date");
        if (date != null) {
            conditions
                    .andBetween("createTime", TimeUtil.getStartTimeOfDate(date), TimeUtil.getOverTimeOfDate(date));
        }
        Page<Recharge> page = getPage(conditions, pageRequestMap);
        return page;
    }

    public void loadUsername(List<Recharge> content) {
        if (content == null)
            return;
        for (Recharge recharge : content) {
            User user = userService.getOne(recharge.getUserId());
            recharge.setUsername(user.getUsername());
        }
    }

    public void agree(Integer id) {
        audit(id, true);
    }

    public void disagree(Integer id) {
        audit(id, false);
    }

    /**
     * 审核
     *
     * @param id
     * @param status true通过,false 不通过
     */
    public void audit(Integer id, boolean status) {
        Optional<Recharge> optional = findById(id);
        Assert.state(optional.isPresent(), "记录不存在");
        Recharge recharge = optional.get();
        Assert.state(Recharge.STATUS_0 == recharge.getStatus(), "已审核过");
        Balance balance = balanceService.findOrCreateByUserIdAndType(recharge.getUserId(), recharge.getBalanceType());
        recharge.setStatus(status ? Recharge.STATUS_1 : Recharge.STATUS_2);
        if (status)
            balanceService.incomeAndLog(balance, recharge.getAmount(), Event.RECHARGE, "充值审核成功");
        recharge.setUpdateTime(new Date());
        balanceService.save(balance);
        save(recharge);
    }

    /**
     * @param username
     * @param amount
     * @param type
     * @param info
     * @param event
     */
    public void rechargeByAdmin(String username, Double amount, Integer type, String info, Event event) {
        User user = userService.findByUsername(username);
        Assert.notNull(user, "用户不存在");
        Balance balance = balanceService.findOrCreateByUserIdAndType(user.getId(), type);
        // 打开插入手动充值记录，fw 2018/4/21/13点57分
        Recharge recharge = new Recharge();
        recharge.setAmount(amount);
        recharge.setBalanceType(type);
        recharge.setCreateTime(new Date());
        recharge.setUpdateTime(new Date());
        recharge.setStatus(Recharge.STATUS_1);
        recharge.setInfo(info);
        recharge.setUserId(user.getId());
        save(recharge);
        balanceService.settlementAndLog(balance, amount, event, info, true);
        balanceService.save(balance);
    }

    /**
     * 充值
     *
     * @param recharge
     */
    public void add(Recharge recharge) {
        Assert.notNull(recharge, "参数错误");
        Assert.notNull(recharge.getAmount(), "参数错误");
        double amount = (int) (double) recharge.getAmount();
        String rechargeWeekDay = settingsService.getSettings().getRechargeWeekDay();
        Assert.state(rechargeWeekDay.contains(String.valueOf(TimeUtil.getDayOfWeek())),
                TimeUtil.getDayNameOfWeek() + "不能充值");
        Double rechargeMin = settingsService.getSettings().getRechargeMin();
        Assert.state(amount >= rechargeMin, "最低充值金额:" + rechargeMin);
        Double rechargeMax = settingsService.getSettings().getRechargeMax();
        Assert.state(amount <= rechargeMax, "最高充值金额:" + rechargeMax);
        Double rechargeFactor = settingsService.getSettings().getRechargeFactor();
        Assert.state(amount % rechargeFactor == 0, "充值的数量必须是" + rechargeFactor + "倍数");

        Double rechargeExchangeRate = settingsService.getSettings().getRechargeExchangeRate();
//        recharge.setExchangeRate(rechargeExchangeRate);
        save(recharge);
    }

}
