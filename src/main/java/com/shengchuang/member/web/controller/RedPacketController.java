package com.shengchuang.member.web.controller;

import com.shengchuang.member.additional.domain.RedPacket;
import com.shengchuang.member.additional.service.RedPacketService;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.member.additional.service.setting.domain.Settings;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.service.BalanceLogService;
import com.shengchuang.base.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import java.util.List;

@Controller
public class RedPacketController extends AbstractController {
    @Autowired
    private RedPacketService redPacketService;
    @Autowired
    private BalanceLogService balanceLogService;
    @Autowired
    private SettingsService settingsService;

    @RequestMapping("/front/redPacket/getAmount")
    public View getRedPacket() {
        User user = getSessionUser();
        Settings settings = settingsService.getSettings();

        List<RedPacket> redPackets = redPacketService.redPacketListByDay(user, false);
        RedPacket redPacket = null;
        if (redPackets != null && redPackets.size() > 0) {
            redPacket = redPackets.get(0);
        } else {
            redPacket = new RedPacket(user.getId(), settings.getMinPacket(), settings.getMaxPacket());
            redPacketService.save(redPacket);
        }
        return new JsonVO(redPacket);
    }

}
