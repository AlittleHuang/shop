package com.shengchuang.member.trading.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

/**
 * 每日统计
 * SELECT
 * `t0`.`date` AS `day`,
 * `t0`.`total` AS `trading`,
 * `t1`.`traded` AS `traded`
 * FROM
 * (
 * SELECT
 * date_format( `t`.`time`, '%Y-%m-%d' ) AS `date`,
 * sum( - `t`.`fees` - `b`.`amount` ) AS `total`
 * FROM
 * ( `ecc`.`trading` `t` LEFT JOIN `ecc`.`balance_log` `b` ON ( ( `t`.`log_id` = `b`.`id` ) ) )
 * GROUP BY
 * date_format( `t`.`time`, '%Y-%m-%d' )
 * ) `t0`
 * LEFT JOIN
 * (
 * SELECT
 * date_format( `t_o`.`time`, '%Y-%m-%d' ) AS `date`,
 * sum( `t_o`.`amount` ) AS `traded`
 * FROM
 * `ecc`.`trade_order` `t_o`
 * WHERE
 * ( `t_o`.`status` = 3 )
 * GROUP BY
 * date_format( `t_o`.`time`, '%Y-%m-%d' )
 * ) `t1`
 * ON `t0`.`date` = `t1`.`date`
 */
@Entity(name = "trading_report")//映射到视图
public class TradingReport {

    /**
     * 日挂卖量
     */
    Double trading;
    /**
     * 日交易量
     */
    Double traded;
    @Id
    @Column(name = "`day`")
    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getTrading() {
        return trading;
    }

    public void setTrading(Double trading) {
        this.trading = trading;
    }

    public Double getTraded() {
        return traded;
    }

    public void setTraded(Double traded) {
        this.traded = traded;
    }
}
