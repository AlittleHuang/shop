package com.shengchuang.member.additional.service.setting.domain;

import com.shengchuang.common.exception.BusinessException;
import com.shengchuang.common.util.Assert;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class Settings {
	
    /**
     * 注册手机号注册个数
     */
    private Integer phoneCount = 3;

    /**
     * 每台矿机初始算力
     */
    private Double machinePower = 200.0;

    /**
     * 存储钱包释放周期(天数)
     */
    private Integer releaseCycle = 90;

    /**
     * 红包最小值
     */
    private Integer minPacket = 5;

    /**
     * 红包最大值
     */
    private Integer maxPacket = 10;

    /**
     * zl 价格
     */
    private Double mmdPrice = 10d;

    private Double priceMmdOfTch = 1000d;

    /**
     * 预期价格
     */
    private Double expectedPrice = 1d;

    /**
     * 母链价格
     */
    private Double parentPrice = 1.4;

    /**
     * 美金汇率
     */
    private Double usdTocny = 6.5;

    /**
     * 母链价格
     */
    private Double uzcPrice = 1.2;

    /**
     * 预期价格时间
     */
    private LocalDate expectedPriceDate = LocalDate.of(2018, 1, 1);

    /**
     * 提现设置
     */
    private String withdrawWeekDay = "0,1,2,3,4,5,6,";// 提现日期(0到6对应星期日到星期六)
    private Double withdrawMin = 100.0;// 最低提现
    private Double withdrawMax = 1000000.0;// 最高提现
    private Double withdrawFee = 0.01;// 提现手续费
    private Double withdrawFactor = 10.0;// 提现倍数
    private double withdrawRateToBalance3 = 0.2;//提现到购物通证百分比

    private String rechargeWeekDay = "0,1,2,3,4,5,6,";//充值日期(0到6对应星期日到星期六)
    private Double rechargeMin = 100.0;//最低充值
    private Double rechargeMax = 1000000.0;//最高充值
    private Double rechargeFactor = 10.0;//充值倍数
    private Double rechargeFee = 0.0;//充值手续费
    private Double rechargeExchangeRate = 1.0;// 充值汇率

    /**
     * 挂买
     **/
    private double epMin = 10;//最低
    private double epMax = 10000000;//最高
    private double epFactor = 10;//倍数
    private double epFee = 0;//手续费
    private int epShowMax = 48;//最多显示条数
    private double tradingAdFees = 0.1;//诚信金
    private Integer buyToAdTimesPerDay = 1;//每天最多购买次数
    private Double buyToAdMaxPerTimes = 1000.0;//每次出售最大数量

    /**
     * 挂卖
     **/
    private double epSellMin = 10;//最低
    private double epSellMax = 10000000;//最高
    private double epSellFactor = 10;//倍数
    private double epSellFee = 0;//手续费
    private int epShowSellMax = 48;//最多显示条数
    private double tradingAdSellFees = 0.1;//诚信金
    private Integer sellToAdTimesPerDay = 1;//每天最多出售次数
    private Double sellToAdMaxPerTimes = 1000.0;//每次出售最大数量

    private Double subBuyerComplete = 3d;//确认打款时间限制
    private Double subSellerComplete = 3d;//确认收款时间限制
    private String epWeekDay = "0,1,2,3,4,5,6,";// 交易日期(0到6对应星期日到星期六)
    private String epTime = "00:00:00 - 23:59:59";//交易时间

    private String bankAccount = "银行卡号未设置";//银行卡号
    private String bankType = "-";//银行
    private String bankUserName = "-";//开户行户名
    private String bankAddress;//开户行地址
    private String tips;//提示信息


    private double registerQK = 100d;

    private int adTradingCount = 5;

    private boolean systemUpdate = false;
    private double price = 1;

    private double fl2mlsCostU = 0.3;
    private double dl2mlsCostU = 0.3;

    private double priceOfUsdt = 1.061;

    public LocalTime[] epTime() {
        String[] split = epTime.split(" - ");
        String error = "EP交易时间范围设置错误";
        Assert.state(split.length == 2, error);

        LocalTime[] times = new LocalTime[2];
        try {
            for (int i = 0; i < split.length; i++) {
                times[i] = LocalTime.parse(split[i]);
            }
        } catch (Exception e) {
            throw new BusinessException(error);
        }
        return times;
    }

}