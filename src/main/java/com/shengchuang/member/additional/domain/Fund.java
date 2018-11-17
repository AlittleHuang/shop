package com.shengchuang.member.additional.domain;

import com.shengchuang.member.core.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity(name = "fund")
public class Fund {

    /**
     * 待审核
     */
    public static final int STATUS_0 = 0;

    /**
     * 审核通过
     */
    public static final int STATUS_1 = 1;

    /**
     * 审核不通过
     */
    public static final int STATUS_2 = 2;

    public static final String[] STATUS_NAME = {"待审核", "审核通过", "审核不通过"};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Integer id;
    /**
     * 关联用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    /**
     * 资产名称
     */
    @Column(name = "fund_type")
    private String fundType;
    /**
     * 类型   0 USC私募    1 USC代购      2
     */
    private Integer type;
    /**
     * 购买数量
     */
    private Double amount;

    /**
     * 当前价格 FIXME 尼玛这价格到底是什么价格 USD? 转入币种?
     */
    private Double price;

    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注信息
     */
    private String info;
    /**
     * 审核信息
     */
    @Column(name = "re_info")
    private String reInfo;
    /**
     * 联系方式
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 转账凭证图片
     */
    private String proof;
    /**
     * 转账时间
     */
    @Column(name = "transfer_time")
    private Date transferTime;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    @Transient
    private String transferTimeStr;

    public String getStatusName() {
        return STATUS_NAME[status];
    }


}
