package com.shengchuang.member.core.service;

import com.shengchuang.common.util.Assert;
import com.shengchuang.member.core.domain.TransferSetting;
import com.shengchuang.member.core.domain.enmus.BalanceType;
import com.shengchuang.base.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferSettingsService extends AbstractService<TransferSetting, Integer> {

    @Transactional
    public TransferSetting addTransferSetting(TransferSetting transferSetting) {
        Integer typeIn = transferSetting.getTypeIn();
        Integer typeOut = transferSetting.getTypeOut();
        Integer transferType = transferSetting.getTransferType();
        Assert.notNull(typeIn, "请输入转入类型");
        Assert.notNull(typeOut, "请输入转出类型");
        Assert.notNull(transferType, "请输入转账类型");
        if (transferType == TransferSetting.CONVERT) {
            Assert.state((int) typeIn != typeOut, "不能设置同种币种之间的兑换");
        }
        boolean exists = createCriteria().andEqual("typeIn", typeIn)
                .andEqual("typeOut", typeOut)
                .andEqual("transferType", transferType)
                .exists();
        Assert.state(!exists, "该类型已设置");
        return save(transferSetting);
    }


    public TransferSetting getSettings(int typeIn, int typeOut, int transferType) {
        return getSettings(BalanceType.of(typeIn), BalanceType.of(typeOut), transferType);
    }

    public TransferSetting getSettings(BalanceType typeIn, BalanceType typeOut, int transferType) {
        Assert.state(typeIn != null, "转入类型错误");
        Assert.state(typeOut != null, "转出类型错误");
        return createCriteria().andEqual("typeIn", typeIn.index)
                .andEqual("typeOut", typeOut.index)
                .andEqual("transferType", transferType)
                .getOne();
    }

}
