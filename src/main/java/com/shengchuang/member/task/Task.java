package com.shengchuang.member.task;

import com.shengchuang.member.additional.service.PriceService;
import com.shengchuang.member.additional.service.SettlementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class Task {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private PriceService priceService;

    @Scheduled(cron = "0 0 3 * * ?")
    public void integritySettlement() {
        LocalDate now = LocalDate.now();

        try {
            settlementService.timingSettlement(SettlementService.Type.DAILY);
        } catch (Exception e) {
            logger.error("每日结算发生错误", e);
        }

        if (now.getDayOfMonth() == 1) {
            try {
                settlementService.timingSettlement(SettlementService.Type.MONTH);
            } catch (Exception e) {
                logger.error("每月结算发生错误", e);
            }
        }

    }

    @Scheduled(cron = "0 * * * * ?")//1分钟运行一次
    public void automatVicirtualMatch() {
        //priceService.updatePrice(System.currentTimeMillis());
    }


}
