package com.shengchuang.member.additional.domain;

import com.shengchuang.common.util.NumberUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity(name = "ore")
public class Ore {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 日期
     */
    private LocalDate date;

    @Column(name = "user_id")
    private Integer userId;

    /**
     * 总数
     */
    private Double total;

    @Version
    private Integer version;

    private Double ore0;
    private Double ore1;
    private Double ore2;
    private Double ore3;
    private Double ore4;
    private Double ore5;

    public Ore(double total, int userId) {
        this.total = total;
        this.userId = userId;
        date = LocalDate.now();
        int oreCount = 6;
        double[] doubles = new double[oreCount];
        double sum = 0;
        for (int i = 0; i < doubles.length; i++) {
            int lft = doubles.length - i;
            double random = 1;
            if (lft != 1)
                random = NumberUtil.nextDouble(1.5, 0.5);
            doubles[i] = NumberUtil.halfUp((total - sum) * (1d / lft * random), 2);
            sum += doubles[i];
        }
        ore0 = doubles[0];
        ore1 = doubles[1];
        ore2 = doubles[2];
        ore3 = doubles[3];
        ore4 = doubles[4];
        ore5 = doubles[5];
    }

    public static void main(String[] args) {
        Ore ore = new Ore(0, 100);
        System.out.println(ore);
    }

    public boolean pickedAll() {
        return ore0 == 0
                && ore1 == 0
                && ore2 == 0
                && ore3 == 0
                && ore4 == 0
                && ore5 == 0;
    }

}
