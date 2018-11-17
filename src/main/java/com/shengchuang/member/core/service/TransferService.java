package com.shengchuang.member.core.service;

import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.member.core.domain.*;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.member.core.domain.enmus.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransferService {

    @Autowired
    private BalanceService balanceService;
    @Autowired
    private TransferSettingsService transferSettingsService;
    @Autowired
    private TransferLogService transferLogService;

    /**
     * 会员互转
     *
     * @param from        转出会员
     * @param target      转入会员
     * @param balanceType 币种类型
     * @param amount      转账数量
     */
    public void transfer(User from, User target, int balanceType, double amount) {
        Assert.state(amount > 0, "数量必须大于0");
        BalanceType type = BalanceType.of(balanceType);
        TransferSetting setting =
                transferSettingsService.getSettings(type, type, TransferSetting.TRANSFER);
        Assert.notNull(setting, "暂未开放");
        settlement(amount, from, target, setting);
    }

    /**
     * 币种兑换
     *
     * @param user    操作用户
     * @param typeOut 转出类型
     * @param typeIn  转入类型
     * @param amount  兑换数量
     */
    public void converted(User user, Integer typeOut, Integer typeIn, Double amount) {
        Assert.state(amount > 0, "数量必须大于0");
        BalanceType in = BalanceType.of(typeIn);
        BalanceType out = BalanceType.of(typeOut);
        TransferSetting transferSetting = transferSettingsService.getSettings(in, out, TransferSetting.CONVERT);
        Assert.notNull(transferSetting, "暂未开放");

        settlement(amount, user, user, transferSetting);
    }

    private void settlement(double amount, User from, User target, TransferSetting setting) {
        setting.checkAmount(amount);

        // 校验每日限额
        Double dailyLimit = setting.getDailyLimit();
        if (dailyLimit != null && dailyLimit > 0) {
            double sumToday = transferLogService.getSumTodey(from.getId(), setting);
            Assert.state(dailyLimit >= sumToday + amount, "已超过每日限额, 剩余额度: " + (dailyLimit - sumToday));
        }

        // 校验额度百分比
        Balance balanceOut = null;
        Double proportion = setting.getProportion();
        if (proportion != null && proportion < 1) {
            balanceOut = balanceService.findOrCreateByUserIdAndType(from.getId(), setting.typeOut());
            Assert.state(
                    amount <= balanceOut.getAmount() * proportion - 0.00001,
                    "每次转出不超过总量的" + NumberUtil.fix2(proportion * 100) + "%"
            );
        }

        // 校验每日转账次数
        Integer frequency = setting.getDailyFrequency();
        if (frequency != null) {
            long count = transferLogService.createCriteria().andEqual("from.userId", from.getId())
                    .andEqual("transferType", setting.getTransferType())
                    .andEqual("from.type", setting.getTypeOut())
                    .andEqual("to.type", setting.getTypeIn())
                    .andBetween("from.time", TimeUtil.getStartTimeToday(), TimeUtil.getOverTimeToday())
                    .count();
            Assert.state(count < frequency, "每日最多转账" + frequency + "次");
        }

        Event event = setting.toBalanceLogType();
        Integer transferType = setting.getTransferType();
        Double price = setting.getPrice();
        double feesAmount = setting.feesAmount(amount);

        String[] fromInfo = {setting.typeOut().name, from.getUsername()};
        String[] targetInfo = {setting.typeIn().name, target.getUsername()};

        String infoOut = "转出至 " + targetInfo[transferType]
                + (price == null || price == 1 ? "" : (", 到账比率 " + price))
                + (feesAmount == 0 ? "" : (", 含手续费 " + feesAmount));

        String infoIn = fromInfo[transferType] + " 转入";

        if (balanceOut == null)
            balanceOut = balanceService.findOrCreateByUserIdAndType(from.getId(), setting.typeOut());
        BalanceLog fLog = balanceService.expenditureAndLog(balanceOut, amount + feesAmount, event, infoOut);

        Balance balanceIn = balanceService.findOrCreateByUserIdAndType(target.getId(), setting.typeIn());
        BalanceLog tLog = balanceService.incomeAndLog(balanceIn, setting.in(amount), event, infoIn);

        TransferLog transferLog = new TransferLog(amount, fLog, tLog, setting.getTransferType(), setting.feesAmount(amount),price);
        transferLogService.save(transferLog);
    }


}
