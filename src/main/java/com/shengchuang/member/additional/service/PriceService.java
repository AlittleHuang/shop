package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.PriceLog;
import com.shengchuang.member.additional.repository.PriceLogRepository;
import com.shengchuang.member.additional.service.setting.SettingsService;
import com.shengchuang.member.additional.service.setting.domain.Settings;
import com.shengchuang.common.util.NumberUtil;
import com.shengchuang.common.util.TimeUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.*;
import java.util.Date;

import static com.shengchuang.common.util.TimeUtil.*;

@Log
@Service
public class PriceService {

    private static final LocalTime start = LocalTime.of(8, 0);
    private static final LocalTime end = LocalTime.of(23, 0);
    @Autowired
    PriceLogRepository priceLogRepository;
    @Autowired
    private SettingsService settingsService;

    public void updatePrice(long now) {
        Date nowDate = new Date(now);
        Instant instant = nowDate.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

        LocalTime nowTime = localDateTime.toLocalTime();
        LocalDate localDateNow = localDateTime.toLocalDate();

        now = now - now % MILLIS_PER_MINUTE;
        Date startOfToday = toDate(localDateNow.atTime(start));//开始时间
        Date endOfToday = toDate(localDateNow.atTime(end));//结束时间
        int minuteInterval = 20;//时间间隔(分钟)
        int count = (int) (now / MILLIS_PER_MINUTE % minuteInterval);//时间间隔内的第几分钟

        if (startOfToday.getTime() > now) return;
        double i = 1 / ((double) minuteInterval - count);
        // log.info(String.valueOf(i));
        if (i < NumberUtil.getRandom().nextDouble()) return;


        Date roundTime = addMinute(nowDate, -count);
        PriceLog log = priceLogRepository.criteria().addOrderByDesc("time").limit(1).getOne();

        Settings settings = settingsService.getSettings();
        LocalDate exDate = settings.getExpectedPriceDate();
        Double expectedPrice = settings.getExpectedPrice();

        if (exDate == null || expectedPrice == null)
            return;
        if (log != null && toDate(exDate.atTime(end)).getTime() <= log.getTime().getTime()) {
            return;
        } else if (log != null && log.getTime().getTime() > roundTime.getTime()) {
            return;
        } else {
            if (localDateNow.equals(exDate) && nowTime.isAfter(end)) {
                if (expectedPrice > settings.getMmdPrice()) {
                    settings.setMmdPrice(expectedPrice);
                    settingsService.save(settings);
                    return;
                }
            } else if (localDateNow.isAfter(exDate)) {
                //throw new RuntimeException();
                return;
            }
        }
        if (nowTime.isAfter(end))
            return;


        long countOneDay = (endOfToday.getTime() - startOfToday.getTime()) / minuteInterval / MILLIS_PER_MINUTE;
        int laveRound = (int) ((endOfToday.getTime() - roundTime.getTime()) / minuteInterval / MILLIS_PER_MINUTE);//剩余次数

        long lftDay = (toDate(exDate.atTime(0, 0, 0)).getTime() - TimeUtil.getStartTimeOfDate(nowDate).getTime()) /
                TimeUtil.MILLIS_PER_DAY;

        long lefCount = countOneDay * lftDay + laveRound;
        double random = NumberUtil.nextDouble(0.8, 1.25);
        double v = expectedPrice - settings.getMmdPrice();
        double priceAdd = v;
        if (lefCount != 0)
            priceAdd = priceAdd / lefCount * random;
        double setPrice = NumberUtil.setScale(settings.getMmdPrice() + priceAdd, 4, RoundingMode.FLOOR);
        settings.setMmdPrice(setPrice);

        settingsService.save(settings);
        PriceLog priceLog = new PriceLog(setPrice, nowDate);
        priceLogRepository.save(priceLog);
    }
}
