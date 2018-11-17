package com.shengchuang.member.additional.service.setting;

import com.shengchuang.member.additional.service.setting.domain.Settings;
import org.springframework.stereotype.Service;

@Service
public class SettingsService extends AbstractSettings<Settings> {

    private static final String KEY_PRE_FIX = "system/bonusSettings/";

    public SettingsService() {
        super(KEY_PRE_FIX, Settings.class);
    }

}
