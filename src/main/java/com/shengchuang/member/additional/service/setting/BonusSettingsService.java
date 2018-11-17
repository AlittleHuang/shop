package com.shengchuang.member.additional.service.setting;

import com.shengchuang.member.additional.service.setting.domain.BonusSettings;
import org.springframework.stereotype.Service;

@Service
public class BonusSettingsService extends AbstractSettings<BonusSettings> {

    private static final String KEY_PRE_FIX = "system/bonusSettings/bonus/";

    public BonusSettingsService() {
        super(KEY_PRE_FIX, BonusSettings.class);
    }

}
