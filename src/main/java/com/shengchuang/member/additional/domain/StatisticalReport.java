package com.shengchuang.member.additional.domain;


import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

/**
 * SELECT
 * date_format( `b`.`time`, '%Y-%m-%d' ) AS `date`,
 * sum( IF ( ( `b`.`type` = 0 ), `b`.`amount`, 0 ) ) AS `amount8`,
 * sum( IF ( ( `b`.`type` = 1 ), `b`.`amount`, 0 ) ) AS `amount9`,
 * sum( IF ( ( `b`.`type` = 2 ), `b`.`amount`, 0 ) ) AS `amount10`,
 * sum( IF ( ( `b`.`type` = 3 ), `b`.`amount`, 0 ) ) AS `amount11`,
 * sum( IF ( ( `b`.`type` = 4 ), `b`.`amount`, 0 ) ) AS `amount12`
 * FROM
 * `balance_log` `b`
 * GROUP BY
 * date_format( `b`.`time`, '%Y-%m-%d' )
 */
@Entity(name = "statistical_report")
public class StatisticalReport {

    @Id
    private LocalDate date;

    private Double amount8;
    private Double amount9;
    private Double amount10;
    private Double amount11;
    private Double amount12;


    public Double getAmount8() {
        return amount8;
    }

    public void setAmount8(Double amount8) {
        this.amount8 = amount8;
    }

    public Double getAmount9() {
        return amount9;
    }

    public void setAmount9(Double amount9) {
        this.amount9 = amount9;
    }

    public Double getAmount10() {
        return amount10;
    }

    public void setAmount10(Double amount10) {
        this.amount10 = amount10;
    }

    public Double getAmount11() {
        return amount11;
    }

    public void setAmount11(Double amount11) {
        this.amount11 = amount11;
    }

    public Double getAmount12() {
        return amount12;
    }

    public void setAmount12(Double amount12) {
        this.amount12 = amount12;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
